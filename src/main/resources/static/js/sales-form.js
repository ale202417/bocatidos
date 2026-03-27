const subtotalPreview = document.getElementById("subtotalPreview");

function updateSalesSubtotalPreview() {
    if (!subtotalPreview) {
        return;
    }

    const qtyInputs = document.querySelectorAll(".line-qty");
    const priceInputs = document.querySelectorAll(".line-price");
    let subtotal = 0;

    qtyInputs.forEach((qtyInput, index) => {
        const quantity = Number(qtyInput.value || 0);
        const price = Number(priceInputs[index]?.value || 0);
        subtotal += quantity * price;
    });

    subtotalPreview.textContent = `$${subtotal.toFixed(2)}`;
}

document.querySelectorAll(".line-qty, .line-price").forEach((input) => {
    input.addEventListener("input", updateSalesSubtotalPreview);
});

updateSalesSubtotalPreview();
