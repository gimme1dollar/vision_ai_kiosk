package com.example.viosk

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity(), MenuAdapter.ListOnClickListener {
    private lateinit var mMenuRecyclerView: RecyclerView
    private lateinit var mRecyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var mRecyclerViewLayoutManager: RecyclerView.LayoutManager

    private lateinit var mPaymentTextView: TextView

    private lateinit var mMenuItems: Array<Pair<String, Int>>
    private var mSelectedMenuItems: MutableMap<String, Int> = mutableMapOf()

    private var mAgeClass: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        mPaymentTextView = findViewById(R.id.tv_pay)
        mAgeClass = intent.getIntExtra("age", 0)
        Log.d("btconnect", mAgeClass.toString())
        if (mAgeClass == 0 || mAgeClass == 3) {
            val moneyButton = findViewById<Button>(R.id.bt_money)
            val callButton = findViewById<Button>(R.id.bt_call)
            val moneyImage = findViewById<ImageView>(R.id.iv_money)
            val callImage = findViewById<ImageView>(R.id.iv_call)

            moneyButton.visibility = View.GONE
            callButton.visibility = View.GONE
            moneyImage.visibility = View.VISIBLE
            callImage.visibility = View.VISIBLE
            mPaymentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 68F)
        }

        mMenuItems = arrayOf(
            Pair("햄버거 1", R.drawable.burger_1),
            Pair("햄버거 2", R.drawable.burger_2),
            Pair("햄버거 3", R.drawable.burger_3),
            Pair("햄버거 4", R.drawable.burger_4),
            Pair("햄버거 세트 1", R.drawable.burger_set),
            Pair("햄버거 세트 2", R.drawable.burger_set2),
            Pair("치킨", R.drawable.chicken),
            Pair("커피", R.drawable.coffee),
            Pair("맥주", R.drawable.beer),
            Pair("코카콜라", R.drawable.coke),
            Pair("쿠키", R.drawable.cookie),
            Pair("우유", R.drawable.milk),
            Pair("피자", R.drawable.pizza),
            Pair("소세지", R.drawable.sausage),
            Pair("스파게티", R.drawable.sphagetti),
            Pair("스프라이트", R.drawable.sprite),
            Pair("토스트", R.drawable.toast)
        )
        if (mAgeClass == 0) {
            mMenuItems[1] = Pair("우유", R.drawable.milk)
            mMenuItems[2] = Pair("쿠키", R.drawable.cookie)
            mMenuItems[3] = Pair("스파게티", R.drawable.sphagetti)
        } else if (mAgeClass == 3) {
            mMenuItems[0] = Pair("햄버거", R.drawable.burger_details)
            mMenuItems[1] = Pair("커피", R.drawable.coffee)
            mMenuItems[2] = Pair("토스트", R.drawable.toast)
        }

        mRecyclerViewLayoutManager =
            if (mAgeClass == 0 || mAgeClass == 3) LinearLayoutManager(this)
            else GridLayoutManager(this, 3)
        mRecyclerViewAdapter = MenuAdapter(mMenuItems, this, mAgeClass == 0 || mAgeClass == 3)
        mMenuRecyclerView = findViewById<RecyclerView>(R.id.rv_menu).apply {
            setHasFixedSize(true)
            layoutManager = mRecyclerViewLayoutManager
            adapter = mRecyclerViewAdapter
        }
    }

    override fun putItem(menuName: String) {
        if (mSelectedMenuItems.containsKey(menuName)) {
            mSelectedMenuItems[menuName] = mSelectedMenuItems[menuName]!!.plus(1)
        } else {
            mSelectedMenuItems[menuName] = 1
        }
        updateBasket()
    }

    private fun updateBasket() {
        val paymentString = mSelectedMenuItems.map { (k, v) ->
            "$k x $v"
        }.joinToString(", ")

        mPaymentTextView.text = paymentString
    }

    fun payment(view: View) {
        val dialog = PaymentDialogFragment(applicationContext)
        dialog.show(supportFragmentManager, "PaymentDialog")
    }

    class PaymentDialogFragment(private val mContext: Context) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("결제하시겠습니까?")
                    .setPositiveButton("예",
                        DialogInterface.OnClickListener { _, _ ->
                            (mContext as Activity).finish()
                        }
                    )
                    .setNegativeButton("아니오",
                        DialogInterface.OnClickListener { _, _ -> }
                    )
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}