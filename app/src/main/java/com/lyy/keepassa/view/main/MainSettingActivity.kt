package com.lyy.keepassa.view.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.arialyy.frame.util.AndroidUtils
import com.lyy.keepassa.R
import com.lyy.keepassa.base.BaseActivity
import com.lyy.keepassa.base.BaseApp
import com.lyy.keepassa.databinding.ActivityChangeDbBinding
import com.lyy.keepassa.event.ModifyDbNameEvent
import com.lyy.keepassa.util.EventBusHelper
import com.lyy.keepassa.util.HitUtil
import com.lyy.keepassa.util.KeepassAUtil
import com.lyy.keepassa.view.dialog.DonateDialog
import com.lyy.keepassa.view.setting.SettingActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode.MAIN

/**
 * 主页下拉设置
 */
class MainSettingActivity : BaseActivity<ActivityChangeDbBinding>(), View.OnClickListener {

  private lateinit var module: MainModule

  override fun onCreate(savedInstanceState: Bundle?) {
    useAnim = false
    super.onCreate(savedInstanceState)
    window.enterTransition.excludeTarget(android.R.id.statusBarBackground, true)
    window.enterTransition.excludeTarget(android.R.id.navigationBarBackground, true)
  }

  companion object {
    const val arrowAnimDuration = 100L
  }

  override fun setLayoutId(): Int {
    return R.layout.activity_change_db
  }

  override fun initData(savedInstanceState: Bundle?) {
    super.initData(savedInstanceState)
    module = ViewModelProvider(this).get(MainModule::class.java)
    EventBusHelper.reg(this)
    binding.kpaToolbar.setOnClickListener(this)
    binding.close.setOnClickListener(this)
    binding.arrow.rotation = 180f
    binding.dbName.text = BaseApp.dbFileName
    binding.dbVersion.text = BaseApp.dbName
    binding.changeSetting.setOnClickListener(this)
    binding.appSetting.setOnClickListener(this)
    binding.changeDb.setOnClickListener(this)
    binding.appFeedback.setOnClickListener(this)
    binding.appFavorite.setOnClickListener(this)
    binding.tvDonate.setOnClickListener(this)
    module.setEcoIcon(this, binding.dbName)
  }

  private fun startArrowAnim() {
    val anim = ObjectAnimator.ofFloat(binding.arrow, "rotation", 180f, 360f)
    anim.duration = arrowAnimDuration
    anim.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        binding.kpaToolbar.setBackgroundColor(resources.getColor(R.color.background_color))
        finishAfterTransition()
        binding.arrow.visibility = View.GONE
      }
    })
    anim.start()
  }

  @Subscribe(threadMode = MAIN)
  fun onDnNameModify(event: ModifyDbNameEvent) {
    binding.dbVersion.text = BaseApp.dbName
  }

  override fun onClick(v: View?) {
    if (KeepassAUtil.isFastClick()) {
      return
    }
    when (v!!.id) {
      R.id.kpa_toolbar, R.id.close -> {
        startArrowAnim()
//        finishAfterTransition()
      }
      R.id.change_setting -> {
        startActivity(
            Intent(this, SettingActivity::class.java).also {
              it.putExtra(SettingActivity.KEY_TYPE, SettingActivity.TYPE_DB)
            },
            ActivityOptions.makeSceneTransitionAnimation(this)
                .toBundle()
        )
      }
      R.id.app_setting -> {
        startActivity(
            Intent(this, SettingActivity::class.java).also {
              it.putExtra(SettingActivity.KEY_TYPE, SettingActivity.TYPE_APP)
            },
            ActivityOptions.makeSceneTransitionAnimation(this)
                .toBundle()
        )
      }
      R.id.change_db -> {
        KeepassAUtil.turnLauncher(this)
      }
      R.id.app_feedback -> {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
          data = Uri.parse("mailto:") // 确保只有邮件应用能接收
          putExtra(Intent.EXTRA_EMAIL, arrayOf("dornkpa@gmail.com"))
          putExtra(Intent.EXTRA_SUBJECT, "Hello, KeepassA")
          putExtra(
              Intent.EXTRA_TEXT,
              getString(
                  R.string.feedback_email_msg, AndroidUtils.getDeviceBrand(),
                  AndroidUtils.getDeviceModel(), AndroidUtils.getSystemVersion(),
                  AndroidUtils.getMetrics(this@MainSettingActivity),
                  AndroidUtils.getVersionName(this@MainSettingActivity)
              )
          )
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
          startActivity(emailIntent)
        } else {
          HitUtil.toaskShort(getString(R.string.send_email_fail))
        }
//        startActivity(Intent(Intent.ACTION_VIEW).apply {
//          data = Uri.parse("https://github.com/AriaLyy/KeepassA/issues")
//        })
      }
      R.id.app_favorite -> {
        if (AndroidUtils.hasAnyMarket(this)) {
          val markIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=com.lyy.keepassa")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          startActivity(markIntent)
        } else {
          HitUtil.toaskShort(getString(R.string.mark_not_exit))
        }
      }
      R.id.tvDonate -> {
        val donateDialog = DonateDialog()
        donateDialog.show()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    EventBusHelper.unReg(this)
  }

}