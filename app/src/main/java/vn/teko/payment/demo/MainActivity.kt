package vn.teko.payment.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import timber.log.Timber
import vn.teko.android.payment.manager.TerraPayment
import vn.teko.android.payment.ui.singlepayment.vnpayewallet.VNPayEWalletCallback
import vn.teko.android.payment.ui.singlepayment.vnpayewallet.VNPayEWalletCustomer
import vn.teko.android.payment.ui.singlepayment.vnpayewallet.VNPayEWalletPaymentRequest
import vn.teko.android.payment.ui.singlepayment.vnpayewallet.VNPayEWalletSubscription
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
    }

    override fun onDestroy() {
        super.onDestroy()
        vnpayEWalletSubscription?.cancel()
    }

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
}