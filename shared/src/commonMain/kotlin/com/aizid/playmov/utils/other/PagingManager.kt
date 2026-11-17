package com.aizid.playmov.utils.other

class PagingManager<T> {
    private var dataBuffer = mutableListOf<T>()
    var currentPage: Int = 1
        private set
    var totalPages: Int = 1
        private set

    fun addData(newData: List<T>, page: Int, totalPages: Int) {
        if (page == 1) {
            dataBuffer = newData.toMutableList()
        } else {
            dataBuffer.addAll(newData)
        }
        this.currentPage = page
        this.totalPages = totalPages
    }

    fun getData(): List<T> = dataBuffer.toList()

    fun clearData() {
        dataBuffer.clear()
        currentPage = 1
        totalPages = 1
    }

    fun hasMorePages(): Boolean = currentPage < totalPages
}
