package com.example.viosk

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity(), MenuAdapter.ListOnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var menuItems: Array<Pair<String, Int>>
    private var selectedItems: MutableMap<String, Int> = mutableMapOf()

    private var ageClass: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        ageClass = intent.getIntExtra("age", 0)
        menuItems = arrayOf(
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

        viewManager = GridLayoutManager(this, 3)
        viewAdapter = MenuAdapter<MenuActivity>(menuItems, this)
        recyclerView = findViewById<RecyclerView>(R.id.rv_menu).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun putItem(menuName: String) {
        if (selectedItems.containsKey(menuName)) {
            selectedItems[menuName] = selectedItems[menuName]!!.plus(1)
        } else {
            selectedItems[menuName] = 1
        }
        updateBasket()
    }

    private fun updateBasket() {
        val items = mutableListOf<String>()
        for (item in selectedItems) {
            items.add(item.key + " x " + item.value.toString())
        }

        val str = items.joinToString(", ")
        val textView = findViewById<TextView>(R.id.tv_pay)
        textView.text = str
    }

    fun pay(view: View) {
        val dialog = PaymentDialogFragment(applicationContext)
        dialog.show(supportFragmentManager, "PaymentDialog")
    }

    class PaymentDialogFragment(private val mContext: Context) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage("결제하시겠습니까?")
                    .setPositiveButton("예",
                        DialogInterface.OnClickListener { dialog, id ->
                            (mContext as Activity).finish()
                        })
                    .setNegativeButton("아니오",
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}