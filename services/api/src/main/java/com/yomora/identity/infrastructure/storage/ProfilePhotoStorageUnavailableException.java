package com.yomora.identity.infrastructure.storage;

public class ProfilePhotoStorageUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProfilePhotoStorageUnavailableException() {
        super("O armazenamento de fotos de perfil ainda não foi configurado");
    }
}
