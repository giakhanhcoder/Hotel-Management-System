package com.example.projectprmt5;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class VietQRConfig {

    // --- THAY THẾ THÔNG TIN CỦA BẠN VÀO ĐÂY ---
    private static final String BANK_ID = "TPBank"; // Ví dụ: Vietcombank
    private static final String ACCOUNT_NO = "04812636501"; // Số tài khoản của bạn
    private static final String ACCOUNT_NAME = "TRAN HUY MINH"; // Tên chủ tài khoản
    // ------------------------------------------

    private static final String TEMPLATE = "compact2";

    // Public getters for other classes to access
    public static String getBankId() {
        return BANK_ID;
    }

    public static String getAccountNo() {
        return ACCOUNT_NO;
    }

    public static String getAccountName() {
        return ACCOUNT_NAME;
    }

    /**
     * Generates a URL for a VietQR image.
     * @param amount The amount to be paid.
     * @param orderInfo The description for the payment.
     * @return A URL string for the QR code image.
     */
    public static String getQrImageUrl(double amount, String orderInfo) {
        long amountValue = (long) amount;
        try {
            String encodedOrderInfo = URLEncoder.encode(orderInfo, "UTF-8");
            return "https://img.vietqr.io/image/"
                    + BANK_ID + "-"
                    + ACCOUNT_NO + "-"
                    + TEMPLATE + ".png?amount="
                    + amountValue + "&addInfo="
                    + encodedOrderInfo + "&accountName="
                    + URLEncoder.encode(ACCOUNT_NAME, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // This should not happen with UTF-8
            return "";
        }
    }
}
