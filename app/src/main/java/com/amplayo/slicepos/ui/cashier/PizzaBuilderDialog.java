package com.amplayo.slicepos.ui.cashier;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.models.OrderItem;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class PizzaBuilderDialog extends DialogFragment {

    public interface OnPizzaConfigured {
        void onConfigured(OrderItem item);
    }

    private OnPizzaConfigured listener;
    private double currentPrice = 0.0;

    public void setListener(OnPizzaConfigured listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_pizza_builder, container, false);

        MaterialButtonToggleGroup toggleSize = v.findViewById(R.id.toggleSize);
        SwitchMaterial switchHalf = v.findViewById(R.id.switchHalf);
        TextInputLayout tilWhole = v.findViewById(R.id.tilWhole);
        TextInputLayout tilLeft = v.findViewById(R.id.tilLeft);
        TextInputLayout tilRight = v.findViewById(R.id.tilRight);
        TextInputEditText etWhole = v.findViewById(R.id.etWhole);
        TextInputEditText etLeft = v.findViewById(R.id.etLeft);
        TextInputEditText etRight = v.findViewById(R.id.etRight);
        Button btnAdd = v.findViewById(R.id.btnAdd);
        AutoCompleteTextView actvCrust = v.findViewById(R.id.actvCrust);

        // Setup Dropdown
        String[] crusts = new String[] { "Hand-Tossed", "Thin Crust", "Stuffed Crust" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                crusts);
        actvCrust.setAdapter(adapter);

        // Logic: Toggle Fields
        switchHalf.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tilWhole.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            tilLeft.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tilRight.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePrice(toggleSize, etWhole, etLeft, etRight, btnAdd);
        });

        // Logic: Price Update Listener
        toggleSize.addOnButtonCheckedListener(
                (group, checkedId, isChecked) -> updatePrice(toggleSize, etWhole, etLeft, etRight, btnAdd));

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePrice(toggleSize, etWhole, etLeft, etRight, btnAdd);
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etWhole.addTextChangedListener(watcher);
        etLeft.addTextChangedListener(watcher);
        etRight.addTextChangedListener(watcher);

        // Logic: Add Button
        btnAdd.setOnClickListener(view -> {
            try {
                int selectedId = toggleSize.getCheckedButtonId();
                String size = (selectedId == R.id.btnSmall) ? "Small"
                        : (selectedId == R.id.btnLarge) ? "Large" : "Medium";

                JSONObject config = new JSONObject();
                config.put("size", size);
                config.put("crust", actvCrust.getText().toString());
                config.put("isHalf", switchHalf.isChecked());

                OrderItem item = new OrderItem();
                item.setName(size + " Pizza");
                item.setUnitPrice(currentPrice);
                item.setQuantity(1);
                item.setLineTotal(currentPrice);
                item.setConfig(config.toString());

                if (listener != null)
                    listener.onConfigured(item);
                dismiss();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        updatePrice(toggleSize, etWhole, etLeft, etRight, btnAdd);
        return v;
    }

    private void updatePrice(MaterialButtonToggleGroup group, TextInputEditText w, TextInputEditText l,
            TextInputEditText r, Button btn) {
        int id = group.getCheckedButtonId();
        double base = (id == R.id.btnSmall) ? 200 : (id == R.id.btnLarge) ? 600 : 400;

        // Count commas as toppings (rough logic for MVP)
        int toppings = 0;
        if (w.getVisibility() == View.VISIBLE) {
            toppings += countToppings(w.getText().toString());
        } else {
            toppings += countToppings(l.getText().toString());
            toppings += countToppings(r.getText().toString());
        }

        currentPrice = base + (toppings * 20); // 20 pesos per topping
        btn.setText(String.format("Add - ₱%.2f", currentPrice));
    }

    private int countToppings(String s) {
        if (s == null || s.trim().isEmpty())
            return 0;
        return s.split(",").length;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
