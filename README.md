# Payment demo

Demonstration for Payment SDK.

## Project setup

### Adding credential for Teko's repository

In `local.properties` file, add lines as belows:

```
TekoPackage.username=<teko-package-username>
TekoPackage.password=<teko-package-token>
```

Notes: Please contact Terra team (tu.nb@teko.vn) to get `userName` and `password` to able to sync.

## VNPay E-Wallet

### openVNPayEWallet

```kotlin
class MainActivity: Activity() {
    private fun openVNPayEWallet() {
        try {
            paymentGateway.openVNPayEWallet(
                context = this,
                customer = VNPayEWalletCustomer(
                    phone = "0999999998",
                    name = "Nguyen",
                    email = "" // optional
                )
            )
        } catch (error: Throwable) {
            Log.e("openVNPayEWallet error", "${error.message}")
        }
    }
}
```

### payWithVNPayEWallet

```kotlin
class MainActivity: Activity() {

    // use vnpayEWalletSubscription for cancelling the current payment with VNPay EWallet -> the callback
    // passed to payWithVNPayEWallet method will not been called
    private var vnpayEWalletSubscription: VNPayEWalletSubscription? = null

    // ...

    fun payWithVNPayEWallet() {
        vnpayEWalletSubscription = paymentGateway.payWithVNPayEWallet(
            context = this,
            request = VNPayEWalletPaymentRequest(
                amount = 100000,
                orderCode = orderCode,
                clientTransactionCode = "client-transaction-code",
                customerPhone = "customer-phone",
                description = "order-description",
                customerName = "customer-name",
                customerEmail = "customer-email"
            ),
            callback = object : VNPayEWalletCallback {
                override fun onResult(result: SinglePaymentResult) {
                    if (result.isSuccess) {
                        // TODO
                    } else {
                        // TODO
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        vnpayEWalletSubscription?.cancel()
    }

    // ...
}
```