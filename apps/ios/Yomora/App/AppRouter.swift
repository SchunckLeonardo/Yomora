import Foundation

enum AppRoute: Hashable {
    case book(Book)
    case libraryBook(LibraryBook)
    case reading(LibraryBook, book: Book?)
    case sessionSummary(SessionSummary)
    case post(Post)
    case composer
    case profile(UUID)
    case followers(UUID)
    case theme
}
