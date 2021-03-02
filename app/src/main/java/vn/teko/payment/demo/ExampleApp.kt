package vn.teko.payment.demo

import android.app.Application
import vn.teko.android.payment.manager.TerraPayment
import vn.teko.terra.core.android.terra.TerraApp

class ExampleApp : Application() {

    override fun onCreate() {
        val terraApp = TerraApp.initializeApp(this)
        super.onCreate()
        TerraPayment.getInstance(this, terraApp).apply {
            config.userId = "Fixed UserId"
        }
    }
}