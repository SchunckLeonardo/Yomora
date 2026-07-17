import PhotosUI
import SwiftUI
import UIKit

struct ProfileView: View {
    let userId: UUID
    let container: AppContainer
    let session: SessionStore
    @State private var model: ProfileViewModel
    @State private var showingEdit = false
    @State private var showingReport = false
    @State private var confirmingBlock = false

    init(userId: UUID, container: AppContainer, session: SessionStore) {
        self.userId = userId
        self.container = container
        self.session = session
        _model = State(initialValue: ProfileViewModel(
            userId: userId,
            isCurrentUser: session.user?.id == userId,
            api: container.api
        ))
    }

    private var profile: UserProfile? { model.profile }
    private var isMe: Bool { model.isCurrentUser }

    var body: some View {
        ScrollView {
            VStack(spacing: 22) {
                UserAvatar(url: profile?.avatarUrl, size: 104)
                VStack(spacing: 4) {
                    Text(profile?.name ?? "Perfil").font(.yomoraTitle)
                    Text("@\(profile?.username ?? "leitor")").foregroundStyle(.secondary)
                }
                Text(profile?.bio ?? "").font(.yomoraEditorial).multilineTextAlignment(.center)
                HStack(spacing: 28) {
                    NavigationLink(value: AppRoute.followers(userId)) { count(profile?.followers ?? 0, "seguidores") }
                    count(profile?.following ?? 0, "seguindo")
                    count(profile?.finishedBooks ?? 0, "concluídos")
                }
                if isMe {
                    SecondaryButton(title: "Editar perfil") { showingEdit = true }
                    NavigationLink {
                        FollowRequestsView(model: model, api: container.api)
                    } label: {
                        HStack {
                            Label("Solicitações para seguir", systemImage: "person.crop.circle.badge.clock")
                            Spacer()
                            if !model.pendingRequests.isEmpty {
                                Text("\(model.pendingRequests.count)")
                                    .font(.caption.bold())
                                    .foregroundStyle(.white)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background(YomoraColor.progressGold, in: Capsule())
                            }
                            Image(systemName: "chevron.right")
                        }
                        .padding()
                        .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 16))
                    }
                    .buttonStyle(.plain)
                } else {
                    PrimaryButton(
                        title: model.followButtonTitle,
                        systemImage: model.followStatus == nil ? "person.badge.plus" : "person.badge.clock"
                    ) { Task { await model.toggleFollow() } }
                }
                HStack {
                    stat("\(profile?.totalReadingMinutes ?? 0)", "minutos", "clock")
                    stat("\(profile?.currentStreak ?? 0)", "dias seguidos", "flame")
                }
                if profile?.publicProfile == false && !isMe {
                    EmptyStateView(title: "Perfil privado", message: "As leituras aparecem após a aprovação.", systemImage: "lock")
                }
                ShareLink(item: URL(string: "https://yomora.app/users/\(userId)")!) { Label("Compartilhar perfil", systemImage: "square.and.arrow.up") }
                if let errorMessage = model.errorMessage {
                    Text(errorMessage).font(.caption).foregroundStyle(.red)
                }
            }.padding()
        }
        .navigationTitle("Perfil").navigationBarTitleDisplayMode(.inline)
        .toolbar {
            if isMe {
                ToolbarItem(placement: .topBarTrailing) {
                    NavigationLink { SettingsView(container: container, session: session) } label: { Image(systemName: "gearshape") }
                }
            } else {
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button { showingReport = true } label: {
                            Label("Denunciar perfil", systemImage: "exclamationmark.bubble")
                        }
                        Button(role: .destructive) { confirmingBlock = true } label: {
                            Label("Bloquear usuário", systemImage: "person.crop.circle.badge.xmark")
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                    }
                }
            }
        }
        .sheet(isPresented: $showingEdit, onDismiss: {
            Task {
                await model.load()
                await session.refreshUser()
            }
        }) {
            if let profile { NavigationStack { ProfileEditView(profile: profile, api: container.api) } }
        }
        .sheet(isPresented: $showingReport) {
            NavigationStack { ReportProfileView(model: model) }
        }
        .confirmationDialog("Bloquear este usuário?", isPresented: $confirmingBlock) {
            Button("Bloquear", role: .destructive) { Task { await model.block() } }
            Button("Cancelar", role: .cancel) { }
        } message: {
            Text("Vocês deixam de ver publicações e perfis um do outro. Uma denúncia não é criada automaticamente.")
        }
        .task { await model.load() }
    }

    private func count(_ value: Int, _ label: String) -> some View {
        VStack { Text("\(value)").font(.title3.bold()); Text(label).font(.caption).foregroundStyle(.secondary) }
    }
    private func stat(_ value: String, _ label: String, _ icon: String) -> some View {
        VStack { Image(systemName: icon).foregroundStyle(YomoraColor.progressGold); Text(value).font(.title2.bold()); Text(label).font(.caption) }
            .frame(maxWidth: .infinity, minHeight: 110).background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 16))
    }
}

private struct FollowRequestsView: View {
    let model: ProfileViewModel
    let api: any APIClientProtocol

    var body: some View {
        List(model.pendingRequests) { request in
            FollowRequestRow(
                request: request,
                api: api,
                approve: { await model.approve(request) },
                reject: { await model.reject(request) }
            )
        }
        .navigationTitle("Solicitações")
        .overlay {
            if model.pendingRequests.isEmpty {
                EmptyStateView(
                    title: "Nenhuma solicitação",
                    message: "Novos pedidos para seguir seu perfil privado aparecem aqui.",
                    systemImage: "person.crop.circle.badge.checkmark"
                )
            }
        }
    }
}

private struct FollowRequestRow: View {
    let request: FollowRequest
    let api: any APIClientProtocol
    let approve: () async -> Void
    let reject: () async -> Void
    @State private var profile: UserProfile?

    var body: some View {
        HStack(spacing: 12) {
            UserAvatar(url: profile?.avatarUrl, size: 44)
            VStack(alignment: .leading, spacing: 2) {
                Text(profile?.name ?? "Leitor").font(.headline)
                Text("@\(profile?.username ?? String(request.followerId.uuidString.prefix(8)))")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            Spacer()
            Button("Recusar") { Task { await reject() } }.buttonStyle(.borderless)
            Button("Aceitar") { Task { await approve() } }.buttonStyle(.borderedProminent)
        }
        .task {
            profile = try? await api.send(
                Endpoint(path: "/api/v1/users/\(request.followerId)"),
                as: UserProfile.self
            )
        }
    }
}

private struct ProfileEditView: View {
    let profile: UserProfile
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var name: TextDraft
    @State private var username: TextDraft
    @State private var bio: TextDraft
    @State private var isPublic: Bool
    @State private var selectedPhoto: PhotosPickerItem?
    @State private var selectedPhotoData: Data?
    @State private var selectedContentType = "image/jpeg"
    @State private var errorMessage: String?
    @State private var isSaving = false
    private let uploader: any ProfilePhotoUploading = ProfilePhotoUploader()

    init(profile: UserProfile, api: any APIClientProtocol) {
        self.profile = profile; self.api = api
        _name = State(initialValue: TextDraft(profile.name)); _username = State(initialValue: TextDraft(profile.username))
        _bio = State(initialValue: TextDraft(profile.bio))
        _isPublic = State(initialValue: profile.publicProfile)
    }
    var body: some View {
        Form {
            Section("Foto de perfil") {
                HStack(spacing: 16) {
                    avatarPreview
                    PhotosPicker(selection: $selectedPhoto, matching: .images) {
                        Label("Escolher foto", systemImage: "photo.on.rectangle")
                    }
                }
                Text("JPEG, PNG ou WebP, com no máximo 5 MB.")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            YomoraDraftField(title: "Nome", draft: name, contentType: .name)
            YomoraDraftField(title: "Nome de usuário", draft: username, contentType: .username)
            DraftTextField(prompt: "Biografia", draft: bio, axis: .vertical).lineLimit(3...8)
            Toggle("Perfil público", isOn: $isPublic)
            if let errorMessage { Text(errorMessage).foregroundStyle(YomoraColor.danger) }
        }
        .navigationTitle("Editar perfil")
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                Button(isSaving ? "Salvando…" : "Salvar") { Task { await save() } }
                    .disabled(isSaving)
            }
        }
        .onChange(of: selectedPhoto) { _, item in
            Task { await loadPhoto(item) }
        }
    }

    @ViewBuilder
    private var avatarPreview: some View {
        if let data = selectedPhotoData, let image = UIImage(data: data) {
            Image(uiImage: image).resizable().scaledToFill()
                .frame(width: 72, height: 72).clipShape(Circle())
        } else {
            UserAvatar(url: profile.avatarUrl, size: 72)
        }
    }

    private func loadPhoto(_ item: PhotosPickerItem?) async {
        guard let item else { return }
        do {
            let data = try await item.loadTransferable(type: Data.self)
            guard let data, let image = UIImage(data: data),
                  let jpegData = preparedAvatarData(image) else {
                errorMessage = "Não foi possível preparar a foto escolhida."
                return
            }
            guard jpegData.count <= 5 * 1024 * 1024 else {
                errorMessage = "Escolha uma imagem com no máximo 5 MB."
                return
            }
            selectedPhotoData = jpegData
            selectedContentType = "image/jpeg"
            errorMessage = nil
        } catch {
            errorMessage = "Não foi possível ler a foto escolhida."
        }
    }

    private func preparedAvatarData(_ image: UIImage) -> Data? {
        let maxDimension: CGFloat = 2_048
        let largestDimension = max(image.size.width, image.size.height)
        let scale = largestDimension > maxDimension ? maxDimension / largestDimension : 1
        let size = CGSize(width: image.size.width * scale, height: image.size.height * scale)
        let normalized = UIGraphicsImageRenderer(size: size).image { _ in
            image.draw(in: CGRect(origin: .zero, size: size))
        }
        return normalized.jpegData(compressionQuality: 0.85)
    }

    private func save() async {
        struct Body: Encodable { let name: String; let username: String; let bio: String; let publicProfile: Bool }
        isSaving = true
        defer { isSaving = false }
        var endpoint = Endpoint(path: "/api/v1/users/me", method: .patch)
        endpoint.body = try? Endpoint.json(Body(
            name: name.value,
            username: username.value,
            bio: bio.value,
            publicProfile: isPublic
        ))
        do {
            _ = try await api.send(endpoint, as: UserProfile.self)
            if let data = selectedPhotoData {
                struct UploadBody: Encodable { let contentType: String; let contentLength: Int }
                var uploadEndpoint = Endpoint(path: "/api/v1/users/me/avatar/upload-url", method: .post)
                uploadEndpoint.body = try Endpoint.json(UploadBody(
                    contentType: selectedContentType,
                    contentLength: data.count
                ))
                let upload = try await api.send(uploadEndpoint, as: ProfilePhotoUpload.self)
                try await uploader.upload(data, to: upload.uploadUrl, contentType: selectedContentType)
                struct CompleteBody: Encodable { let objectKey: String }
                var completeEndpoint = Endpoint(path: "/api/v1/users/me/avatar/complete", method: .post)
                completeEndpoint.body = try Endpoint.json(CompleteBody(objectKey: upload.objectKey))
                _ = try await api.send(completeEndpoint, as: UserProfile.self)
            }
            dismiss()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

private struct ReportProfileView: View {
    let model: ProfileViewModel
    @Environment(\.dismiss) private var dismiss
    @State private var reason = "HARASSMENT"
    @State private var details = ""
    @State private var isSending = false

    var body: some View {
        Form {
            Picker("Motivo", selection: $reason) {
                Text("Assédio ou ofensa").tag("HARASSMENT")
                Text("Spam").tag("SPAM")
                Text("Conteúdo impróprio").tag("INAPPROPRIATE")
                Text("Outro").tag("OTHER")
            }
            TextField("Detalhes opcionais", text: $details, axis: .vertical)
                .lineLimit(3...8)
            Text("A denúncia é enviada para análise e não bloqueia o perfil automaticamente.")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .navigationTitle("Denunciar perfil")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
            ToolbarItem(placement: .confirmationAction) {
                Button("Enviar") { Task { await submit() } }.disabled(isSending)
            }
        }
    }

    private func submit() async {
        isSending = true
        await model.report(reason: reason, details: details)
        isSending = false
        if model.didSubmitReport { dismiss() }
    }
}

struct FollowersView: View {
    let userId: UUID
    let api: any APIClientProtocol
    @State private var users: [UUID] = []
    var body: some View {
        List(users, id: \.self) { id in NavigationLink(value: AppRoute.profile(id)) { Label(id.uuidString.prefix(8), systemImage: "person.crop.circle") } }
            .navigationTitle("Seguidores")
            .overlay { if users.isEmpty { EmptyStateView(title: "Ainda sem seguidores", message: "Compartilhe boas leituras e a comunidade chega.", systemImage: "person.2") } }
            .task { users = (try? await api.send(Endpoint(path: "/api/v1/users/\(userId)/followers"), as: [UUID].self)) ?? [] }
    }
}
