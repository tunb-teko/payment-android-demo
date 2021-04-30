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

Check out more details about VNPay EWallet at [sdkwallet](https://demomb.vnpay.vn:20157/sdk/sdkwallet/).

**Account info:**

- phone: `0999999998`
- name: `Nguyen`
- OTP: `123456`
- PIN: `123456`

Example: [MainActivity](app/src/main/java/vn/teko/payment/demo/MainActivity.kt)

### openVNPayEWallet

```kotlin
class MainActivity: Activity() {
    private fun openVNPayEWalletDeprecated() {
        try {
            // this method is deprecated, please use `VNPayEWalletUtils.openVNPayEWallet` instead
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

    private fun openVNPayEWallet() {
        try {
            VNPayEWalletUtils.openVNPayEWallet(
                paymentGateway = paymentGateway,
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

### getUserInfo

```kotlin
class MainActivity: Activity() {
    private fun getUserInfo() {
        VNPayEWalletUtils.getUserInfo(
            paymentGateway = paymentGateway,
            context = this,
            phoneNumber = "0999999998"
        ) { responseCode: Int, dataJson: String ->

            /*
            // Format data query
            {
                // mã code:
                // 0 => Thành công
                // -1 => User chưa liên kết với SDK Ví, các mã lỗi khác của sdk chỉ handle show message
                // -2 => Số điện thoại, partner ID truyền vào rỗng hoặc NULL
                // -3 => Có lỗi khi request API phía client ví (timeout, nullpointer, ...)
                // -4 => Có lỗi khi request API phía server ví, có kèm theo mã lỗi  của server.
                "code": 0,
                "desc": "Success", // response mesage
                "data": {
                    "kyc_status": "1", // trạng thái kyc: 1 - kyc đã được duyệt; 0 - chưa kyc hoặc đã kyc nhưng chưa đc duyệt
                    "balance": "20000", // số dư ví
                    "bank_linked": 0 // trạng thái liên kết ngân hàng: 1 - đã lknh; 0 - chưa lknh
                }
            }
             */

            val userInfoResponse = Gson().fromJson(dataJson, UserInfoResponse::class.java)

            AlertDialog.Builder(this)
                .setMessage("Response code: $responseCode\nUser info: $userInfoResponse")
                .setPositiveButton("OK") { _, _ -> }
                .create()
                .show()

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
