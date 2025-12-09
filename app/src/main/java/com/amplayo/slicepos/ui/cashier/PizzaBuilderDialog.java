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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.models.OrderItem;
import com.amplayo.slicepos.data.models.PizzaFlavor;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        // Setup Flavor Selection with Prices
        List<PizzaFlavor> flavors = new ArrayList<>();
        flavors.add(new PizzaFlavor("Cheese", 400.00));
        flavors.add(new PizzaFlavor("Pepperoni", 450.00));
        flavors.add(new PizzaFlavor("Hawaiian", 450.00));
        flavors.add(new PizzaFlavor("Veggie", 480.00));
        flavors.add(new PizzaFlavor("Meat Lovers", 550.00));

        RecyclerView rvFlavors = v.findViewById(R.id.rvPizzaFlavors);
        FlavorAdapter flavorAdapter = new FlavorAdapter(flavors, new FlavorAdapter.OnFlavorSelectedListener() {
            @Override
            public void onFlavorSelected(PizzaFlavor flavor, int position) {
                Toast.makeText(requireContext(), "Selected: " + flavor.getName() + " - ₱" + flavor.getPrice(),
                        Toast.LENGTH_SHORT).show();
                // Update the price based on selected flavor
                currentPrice = flavor.getPrice();
                updatePrice(toggleSize, etWhole, etLeft, etRight, btnAdd);
            }
        });
        rvFlavors.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFlavors.setAdapter(flavorAdapter);

        // Initialize with first flavor price (Cheese - ₱400)
        currentPrice = flavors.get(0).getPrice();

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

                // Count toppings to get final price
                int toppings = 0;
                if (etWhole.getVisibility() == View.VISIBLE) {
                    toppings += countToppings(etWhole.getText().toString());
                } else {
                    toppings += countToppings(etLeft.getText().toString());
                    toppings += countToppings(etRight.getText().toString());
                }

                // Calculate size extra
                double sizeExtra = 0.0;
                if (selectedId == R.id.btnMedium) {
                    sizeExtra = 50.0;
                } else if (selectedId == R.id.btnLarge) {
                    sizeExtra = 100.0;
                }

                double finalPrice = currentPrice + sizeExtra + (toppings * 20);

                JSONObject config = new JSONObject();
                config.put("size", size);
                config.put("crust", actvCrust.getText().toString());
                config.put("isHalf", switchHalf.isChecked());

                OrderItem item = new OrderItem();
                item.setName(size + " Pizza");
                item.setUnitPrice(finalPrice);
                item.setQuantity(1);
                item.setLineTotal(finalPrice);
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
        // Use the currentPrice set by flavor selection as the base
        double base = currentPrice;

        // Add size-based pricing: Small = +0, Medium = +50, Large = +100
        int selectedId = group.getCheckedButtonId();
        double sizeExtra = 0.0;
        if (selectedId == R.id.btnMedium) {
            sizeExtra = 50.0;
        } else if (selectedId == R.id.btnLarge) {
            sizeExtra = 100.0;
        }
        // btnSmall or no selection = 0 extra

        // Count commas as toppings (rough logic for MVP)
        int toppings = 0;
        if (w.getVisibility() == View.VISIBLE) {
            toppings += countToppings(w.getText().toString());
        } else {
            toppings += countToppings(l.getText().toString());
            toppings += countToppings(r.getText().toString());
        }

        double finalPrice = base + sizeExtra + (toppings * 20); // Flavor + Size + Toppings
        btn.setText(String.format("Add - ₱%.2f", finalPrice));
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
