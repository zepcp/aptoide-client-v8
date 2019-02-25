package cm.aptoide.pt.editorialList

data class EditorialListViewModel(val curationCards: List<CurationCard>, val offset: Int, val total: Int, var loading: Boolean = false, var error: Error? = null) {

    constructor(loading: Boolean) : this(emptyList<CurationCard>(), -1, -1, loading = true)

    constructor(error: Error?) : this(emptyList<CurationCard>(), -1, -1, error = error)

    fun hasError(): Boolean = error != null
}

enum class Error {
    NETWORK, GENERIC
}
