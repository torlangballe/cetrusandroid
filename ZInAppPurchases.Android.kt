
//
//  ZInAppPurchases.swift
//
//  Created by Tor Langballe on /3/9/16.
//
package com.github.torlangballe.cetrusandroid

data class ZInAppProduct(
        var sid:String = "",
        var name:String = "",
        var price:String = "") {
}

class ZInAppPurchases{
//    var allSKProducts = mutableListOf<SKProduct>()
    var saveStoreCountryCodeFunc: ((ccode: String) -> Unit)? = null
    var handlePurchaseSuccess: ((productId: String, done: () -> Unit) -> Unit)? = null
    //    private var purchasedProductIdentifiers = Set()
//    var productsRequest: SKProductsRequest? = null
    var gotProductsRequestHandler: ((products: List<ZInAppProduct>, error: ZError?) -> Unit)? = null

    fun RequestProducts(ids: MutableList<String>, got: (products: MutableList<ZInAppProduct>, error: ZError?) -> Unit) {
//        productsRequest?.cancel()
//        gotProductsRequestHandler = got
//        productsRequest = SKProductsRequest(productIdentifiers = ids)
//        productsRequest!!.delegate = this
//        productsRequest!!.start()
    }
/*
    fun productsRequest(request: SKProductsRequest, response: SKProductsResponse) {
        val formatter = NumberFormatter()
        formatter.formatterBehavior = .behavior10_4
                formatter.numberStyle = .currency
        var products = mutableListOf<ZInAppProduct>()
        var first = true
        for (skp in response.products) {
            var p = ZInAppProduct()
            p.sid = skp.productIdentifier
            formatter.numberStyle = .currencyISOCode
                    formatter.locale = skp.priceLocale
            p.price = formatter.string(from = skp.price) ?: "?"
            p.name = skp.localizedTitle
            if (p.name.isEmpty()) {
                p.name = ZStr.Join(ZStr.SplitCamelCase(p.sid), sep = " ")
            }
            products.append(p)
            if (first) {
                first = false
                val ccode = (skp.priceLocale as NSLocale).object(forKey = .countryCode) as? String
                if (ccode != null) {
                    saveStoreCountryCodeFunc?.invoke(ccode.lowercased())
                }
            }
        }
        allSKProducts = response.products
        gotProductsRequestHandler?.invoke(products, null)
        clearRequestAndHandler()
    }

    fun request(request: SKRequest, error: ZError) {
        ZDebug.Print("Failed to load list of products:", error.localizedDescription)
        gotProductsRequestHandler?.invoke(mutableListOf(), error)
        clearRequestAndHandler()
    }

    private fun clearRequestAndHandler() {
        productsRequest = null
        gotProductsRequestHandler = null
    }
*/
    fun BuyProduct(sid: String) {
//        if (!SKPaymentQueue.canMakePayments()) {
//            ZAlert.Say(ZTS("You are not set up to purchase in-app payments."))
//            // dialog box when user tries to buy in-app-purchase
//            return
//        }
//        val i = allSKProducts.indexWhere({ it.productIdentifier == sid })
//        if (i != null) {
//            val product = allSKProducts[i]
//            val payment = SKPayment(product = product)
//            ZKeyValueStore.SetBool(true, key = "ZInAppPurchasesInProgress")
//            SKPaymentQueue.default().add(payment)
//        } else {
//            ZAlert.Say(ZTS("Couldn't find that product to purchase. Strange error. Maybe restart app."))
//            // dialog box when user tries to buy in-app-purchase, but something weird happens
//        }
    }

    fun CheckPurchasedItems() {
//        SKPaymentQueue.default().restoreCompletedTransactions()
    }
/*
    fun paymentQueueRestoreCompletedTransactionsFinished(queue: SKPaymentQueue) {
        for (transaction in queue.transactions) {
            val prodId = transaction.payment.productIdentifier
            ZAlert.Say(ZTS("Restored purchase: ") + prodId)
            handlePurchaseSuccess?.invoke(prodId) {   ->  }
        }
    }

    fun failedTransaction(transaction: SKPaymentTransaction) {
        // https://developer.apple.com/library/content/releasenotes/General/iOS93APIDiffs/Swift/StoreKit.html
        ZDebug.Print("SK.failedTransaction state:", transaction.transactionState, transaction.error)
        finishTransaction(transaction, wasSuccessful = false)
    }

    fun finishTransaction(transaction: SKPaymentTransaction, wasSuccessful: Boolean) {
        ZKeyValueStore.SetBool(false, key = "ZInAppPurchasesInProgress")
        if (wasSuccessful) {
            val productId = transaction.payment.productIdentifier
            handlePurchaseSuccess?.invoke(productId, {   ->
                SKPaymentQueue.default().finishTransaction(transaction)
            })
        } else {
            SKPaymentQueue.default().finishTransaction(transaction)
        }
    }

    fun paymentQueue(queue: SKPaymentQueue, updatedTransactions: List<SKPaymentTransaction>) {
        for (transaction in updatedTransactions) {
            when (transaction.transactionState) {
                    .purchased, .restored -> this.finishTransaction(transaction, wasSuccessful = true)
                    .failed -> failedTransaction(transaction)
                    .purchasing -> print("Purchasing...")
            }
        }
    }
*/
    fun SetAsObserver() {
//        SKPaymentQueue.default().add(this)
        if (ZKeyValueStore.BoolForKey("ZInAppPurchasesInProgress")) {
            CheckPurchasedItems()
        }
    }
}
