package dandu.andrei.farmersmarket.Validators;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class TextValidator implements TextWatcher {
    protected EditText inputEditText;
    protected TextInputLayout inputLayout;


    public TextValidator(EditText inputEditText,TextInputLayout inputLayout) {
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
        validateText();
    }
    public void validateText(){
        if(!inputEditText.getText().toString().equals("")){

        }


    }
}
