package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import kasper_external_apps.android.alarmer.R;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Time", "به دستیار زمان بندی و برنامه ریزی Time خوش آمدید :)", R.drawable.time_icon_mini, getResources().getColor(R.color.colorIntro1)));
        addSlide(AppIntroFragment.newInstance("نمایش تاریخ", "در بخش بالای صفحه ی اپ می توانید تاریخ شمسی امروز را مشاهده کنید .", R.drawable.intro0, getResources().getColor(R.color.colorIntro2)));
        addSlide(AppIntroFragment.newInstance("یادآور جدید", "با زدن این دکمه می توانید وارد صفحه ی ساخت یک یادآور جدید شوید .", R.drawable.intro1, getResources().getColor(R.color.colorIntro3)));
        addSlide(AppIntroFragment.newInstance("تنظیم یادآور", "می توانید ویژگی های یادآور را در این صفحه تنظیم کنید.", R.drawable.intro2, getResources().getColor(R.color.colorIntro4)));
        addSlide(AppIntroFragment.newInstance("یادآور تکرار شونده", "با زدن این دکمه صفحه ی تنظیم حالت تکرار باز می شود که می توانید روز هایی از هفته که یادآور باید تکرار شود را انتخاب کنید .", R.drawable.intro3, getResources().getColor(R.color.colorIntro5)));
        addSlide(AppIntroFragment.newInstance("ویرایش یادآور", "با انتخاب کردن یک یادآور از لیست یادآور ها می توانید وارد بخش تنظیمات آن شوید و آن را ویرایش یا حذف کنید.", R.drawable.intro5, getResources().getColor(R.color.colorIntro6)));
        addSlide(AppIntroFragment.newInstance("کار انجام شده", "در صورتی که زمان کار تنظیم شده فرا برسد توسط اپ یادآوری می شود و اگر در حالت تکرار نباشد , به صورت خودکار به بخش فعالیت های کامل شده منتقل می شود. همچنین خودتان نیز می توانید با زدن این دکمه در کنار یادآور , این کار را انجام دهید .", R.drawable.intro4, getResources().getColor(R.color.colorIntro2)));
        addSlide(AppIntroFragment.newInstance("کار های انجام شده", "با وارد شدن به این بخش به لیست کار های انجام شده دسترسی خواهید داشت .", R.drawable.intro6, getResources().getColor(R.color.colorIntro3)));
        addSlide(AppIntroFragment.newInstance("یادآور مهم", "با استفاده از این دکمه در تنظیمات یک یادآور می توانید آن را به عنوان یک فعالیت پر اهمیت علامت گذاری کنید.", R.drawable.intro7, getResources().getColor(R.color.colorIntro4)));
        addSlide(AppIntroFragment.newInstance("یادآور های مهم", "و در این بخش به لیست یادآور های علامت گذاری شده دسترسی خواهید داشت .", R.drawable.intro8, getResources().getColor(R.color.colorIntro5)));
        addSlide(AppIntroFragment.newInstance("استفاده ی دوباره", "با استفاده از این دکمه می توانید یادآور کار انجام شده را دوباره فعال و سپس برای استفاده ی مجدد ویرایش کنید .", R.drawable.intro9, getResources().getColor(R.color.colorIntro6)));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {

        super.onDonePressed(currentFragment);

        startActivity(new Intent(this, AlarmMainActivity.class));

        this.finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

        super.onSkipPressed(currentFragment);

        startActivity(new Intent(this, AlarmMainActivity.class));

        this.finish();
    }
}