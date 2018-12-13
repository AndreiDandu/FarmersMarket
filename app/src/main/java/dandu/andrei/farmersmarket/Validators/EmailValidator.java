package dandu.andrei.farmersmarket.Validators;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public class EmailValidator implements TextWatcher {
    protected EditText inputEditText;
    protected TextInputLayout inputLayout;


    public EmailValidator(EditText inputEditText,TextInputLayout inputLayout) {
        this.inputEditText = inputEditText;
        this.inputLayout = inputLayout;

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    private boolean validateEmail() {
        String email = inputEditText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayout.setError("A valid mail is required");

            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
