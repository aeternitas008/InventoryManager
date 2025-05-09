package Utils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Helper {
    public static void println(String string) {
        System.out.println(string);
    }

    public static void setupUI(final View view, final Context context) {
        // Если элемент EditText, назначаем ему слушатель изменения фокуса
        if (view instanceof EditText) {
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v, context);
                    }
                }
            });
        }

        // Если элемент является контейнером, рекурсивно проходим по всем дочерним элементам
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
