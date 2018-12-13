package dandu.andrei.farmersmarket.Validators;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PasswordValidator implements TextWatcher {
    protected EditText inputEditText;
    protected TextInputLayout inputLayout;

    public PasswordValidator(EditText inputEditText,TextInputLayout inputLayout) {
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
        validatePassword();
    }
    private boolean validatePassword() {
        if (inputEditText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Enter valid password");
            return false;
        }
        if(inputEditText.getText().toString().length() < 6){
            inputLayout.setError("Password too short min 6 characters");
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }
}
