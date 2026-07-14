import SwiftUI

struct ProfileView: View {
    let userId: UUID
    let container: AppContainer
    let session: SessionStore
    @State private var profile: UserProfile?
    @State private var showingEdit = false

    private var isMe: Bool { session.user?.id == userId }

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
                } else {
                    PrimaryButton(title: "Seguir", systemImage: "person.badge.plus") { Task { await follow() } }
                }
                HStack {
                    stat("\(profile?.totalReadingMinutes ?? 0)", "minutos", "clock")
                    stat("\(profile?.currentStreak ?? 0)", "dias seguidos", "flame")
                }
                if profile?.publicProfile == false && !isMe {
                    EmptyStateView(title: "Perfil privado", message: "As leituras aparecem após a aprovação.", systemImage: "lock")
                }
                ShareLink(item: URL(string: "https://yomora.app/users/\(userId)")!) { Label("Compartilhar perfil", systemImage: "square.and.arrow.up") }
            }.padding()
        }
        .navigationTitle("Perfil").navigationBarTitleDisplayMode(.inline)
        .toolbar {
            if isMe { ToolbarItem(placement: .topBarTrailing) { NavigationLink { SettingsView(container: container, session: session) } label: { Image(systemName: "gearshape") } } }
        }
        .sheet(isPresented: $showingEdit, onDismiss: { Task { await load() } }) {
            if let profile { NavigationStack { ProfileEditView(profile: profile, api: container.api) } }
        }
        .task { await load() }
    }

    private func load() async {
        profile = try? await container.api.send(Endpoint(path: isMe ? "/api/v1/users/me" : "/api/v1/users/\(userId)"), as: UserProfile.self)
    }

    private func follow() async {
        var endpoint = Endpoint(path: "/api/v1/users/\(userId)/follow", method: .post); endpoint.body = Data("{}".utf8)
        try? await container.api.sendVoid(endpoint); await load()
    }

    private func count(_ value: Int, _ label: String) -> some View {
        VStack { Text("\(value)").font(.title3.bold()); Text(label).font(.caption).foregroundStyle(.secondary) }
    }
    private func stat(_ value: String, _ label: String, _ icon: String) -> some View {
        VStack { Image(systemName: icon).foregroundStyle(YomoraColor.progressGold); Text(value).font(.title2.bold()); Text(label).font(.caption) }
            .frame(maxWidth: .infinity, minHeight: 110).background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 16))
    }
}

private struct ProfileEditView: View {
    let profile: UserProfile
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var name: String
    @State private var username: String
    @State private var bio: String
    @State private var avatar: String
    @State private var isPublic: Bool

    init(profile: UserProfile, api: any APIClientProtocol) {
        self.profile = profile; self.api = api
        _name = State(initialValue: profile.name); _username = State(initialValue: profile.username)
        _bio = State(initialValue: profile.bio); _avatar = State(initialValue: profile.avatarUrl ?? "")
        _isPublic = State(initialValue: profile.publicProfile)
    }
    var body: some View {
        Form {
            YomoraTextField(title: "Nome", text: $name)
            YomoraTextField(title: "Nome de usuário", text: $username)
            YomoraTextField(title: "URL do avatar", text: $avatar, keyboard: .URL)
            TextField("Biografia", text: $bio, axis: .vertical).lineLimit(3...8)
            Toggle("Perfil público", isOn: $isPublic)
        }
        .navigationTitle("Editar perfil")
        .toolbar { ToolbarItem(placement: .confirmationAction) { Button("Salvar") { Task { await save() } } } }
    }
    private func save() async {
        struct Body: Encodable { let name: String; let username: String; let bio: String; let avatarUrl: String?; let publicProfile: Bool }
        var endpoint = Endpoint(path: "/api/v1/users/me", method: .patch)
        endpoint.body = try? Endpoint.json(Body(name: name, username: username, bio: bio, avatarUrl: avatar.isEmpty ? nil : avatar, publicProfile: isPublic))
        if (try? await api.send(endpoint, as: UserProfile.self)) != nil { dismiss() }
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
