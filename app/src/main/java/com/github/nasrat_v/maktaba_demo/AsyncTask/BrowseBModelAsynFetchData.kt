package com.github.nasrat_v.maktaba_demo.AsyncTask

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.github.nasrat_v.maktaba_demo.ICallback.IInputBrowseCallback
import com.github.nasrat_v.maktaba_demo.Listable.Book.Horizontal.Model.BModel
import com.github.nasrat_v.maktaba_demo.Listable.Book.Model.BrowseBModel
import com.github.nasrat_v.maktaba_demo.Listable.Book.Vertical.ListModel.ListBModel
import com.github.nasrat_v.maktaba_demo.Listable.Genre.GModel
import com.github.nasrat_v.maktaba_demo.Services.Provider.Book.BModelProvider
import com.github.nasrat_v.maktaba_demo.Services.Provider.Genre.GModelProvider

class BrowseBModelAsynFetchData(
    context: Context,
    private var inputCallback: IInputBrowseCallback,
    private var languageCode: String
) :
    androidx.loader.content.AsyncTaskLoader<BrowseBModel>(context) {

    override fun loadInBackground(): BrowseBModel? {
        //android.os.Debug.waitForDebugger()

        val allBooksFromDatabase = fetchAllBooksFromDatabase()
        val booksResult = arrayListOf<BModel>()
        val booksByGenreResult = arrayListOf<ListBModel>()

        findBooks(booksResult, allBooksFromDatabase)
        if (booksResult.isNotEmpty()) {
            findBooksByGenre(booksByGenreResult, booksResult.first().genre)
        }
        return BrowseBModel(booksResult, booksByGenreResult)
    }

    private fun fetchAllBooksFromDatabase(): ArrayList<BModel> {
        return BModelProvider(context, languageCode).getAllBooksFromDatabase()
    }

    private fun findBooks(result: ArrayList<BModel>, allBooksFromDatabase: ArrayList<BModel>) {
        val inputString = inputCallback.getInputBrowseString()

        result.addAll(
            allBooksFromDatabase.filter {
                isSearchMatching(it, inputString)
            }
        )
    }

    private fun findBooksByGenre(result: ArrayList<ListBModel>, genre: GModel) {
        val list = GModelProvider(context, languageCode).getAllBooksFromGenre(genre)

        result.add(ListBModel(("Category: " + genre.name), list))
    }

    private fun isSearchMatching(book: BModel, str: String): Boolean {
        return (book.title.toLowerCase().contains(str) ||
                book.author.name.toLowerCase().contains(str) ||
                book.country.toLowerCase().contains(str) ||
                book.genre.name.toLowerCase().contains(str) ||
                book.publisher.toLowerCase().contains(str))
    }
}