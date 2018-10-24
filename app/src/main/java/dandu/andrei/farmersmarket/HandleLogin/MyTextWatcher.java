package dandu.andrei.farmersmarket.HandleLogin;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import dandu.andrei.farmersmarket.R;

public class MyTextWatcher implements TextWatcher {
    protected EditText inputEditText;
    protected TextInputLayout inputLayout;


    public MyTextWatcher(EditText inputEditText,TextInputLayout inputLayout) {
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

            switch (inputEditText.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }

    private boolean validateEmail() {
        String email = inputEditText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayout.setError("Enter valid email");
          //  requestFocus(inputEmail);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputEditText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Enter valid password");
          //  requestFocus(inputPassword);
            return false;
        }
        if(inputEditText.getText().toString().length() < 6){
            inputLayout.setError("Password too short min 6 chr");
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }
//    private void requestFocus(View view) {
//        if (view.requestFocus()) {
//            .getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
