package vn.teko.payment.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okio.GzipSource
import timber.log.Timber
import vn.teko.android.payment.manager.TerraPayment
import vn.teko.android.payment.ui.singlepayment.vnpayewallet.*
import vn.teko.android.payment.ui.util.extension.openVNPayEWallet
import vn.teko.android.payment.ui.util.extension.payWithVNPayEWallet
import vn.teko.android.payment.v2.IPaymentGateway
import vn.teko.android.payment.v2.model.exposing.result.SinglePaymentResult
import vn.teko.demo.miniapp.R
import vn.teko.terra.core.android.terra.TerraApp
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var paymentGateway: IPaymentGateway
    private var vnpayEWalletSubscription: VNPayEWalletSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paymentGateway = TerraPayment.getInstance(
            this,
            TerraApp.getInstance()
        )

        findViewById<Button>(R.id.btnOpenVNPayEWallet).setOnClickListener {
            openVNPayEWallet()
        }

        findViewById<Button>(R.id.btnPayWithVNPayEWallet).setOnClickListener {
            payWithVNPayEWallet()
        }

        findViewById<Button>(R.id.btnGetUserInfo).setOnClickListener {
            getUserInfo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vnpayEWalletSubscription?.cancel()
    }

    private fun openVNPayEWalletDeprecated() {
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

    private fun payWithVNPayEWallet() {
        val orderCode = UUID.randomUUID().toString().substring(0, 12)
        vnpayEWalletSubscription = paymentGateway.payWithVNPayEWallet(
            context = this,
            request = VNPayEWalletPaymentRequest(
                amount = 100000,
                orderCode = orderCode,
                clientTransactionCode = "clientTransactionCode-$orderCode",
                customerPhone = "0999999998",
                description = "",
                customerName = "Nguyen",
                customerEmail = ""
            ),
            callback = object :
                VNPayEWalletCallback {
                override fun onResult(result: SinglePaymentResult) {
                    Timber.i(result.error)
                    Toast.makeText(
                        this@MainActivity,
                        "Result: ${result.isSuccess}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        )
    }

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

data class UserInfoResponse(
    @SerializedName("code")
    val code: String = "",

    @SerializedName("desc")
    val desc: String = "",

    @SerializedName("data")
    val data: Data? = null
) {
    data class Data(
        @SerializedName("kyc_status")
        val kycStatus: String = "",

        @SerializedName("balance")
        val balance: String = "",

        @SerializedName("bank_linked")
        val bankLinked: String = ""
    )
}