package com.krushiadhaar.app.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.krushiadhaar.app.GlobalMockData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateAndShareReport(context: Context): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint().apply {
            color = Color.rgb(46, 125, 50) // GreenPrimary
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
        }
        val boldPaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textPaint = Paint().apply {
            color = Color.rgb(60, 60, 60)
            textSize = 10f
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        val cardPaint = Paint().apply {
            color = Color.rgb(245, 247, 245)
            style = Paint.Style.FILL
        }

        var y = 40f

        // 1. Header Banner
        canvas.drawRoundRect(20f, y, 575f, y + 60f, 12f, 12f, cardPaint)
        canvas.drawText("KRISHI-AADHAR — FARM HEALTH & REVENUE REPORT", 35f, y + 26f, titlePaint)
        val dateStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on: $dateStr  |  Govt ID / Aadhaar Verified: YES", 35f, y + 46f, subPaint)

        y += 85f

        // 2. Farmer & Land Profile
        canvas.drawText("1. FARMER & LAND PROFILE", 25f, y, boldPaint)
        y += 6f
        canvas.drawLine(25f, y, 570f, y, linePaint)
        y += 18f

        val farmerName = com.krushiadhaar.app.GlobalMockData.farmerName.value
        val farmerLoc = com.krushiadhaar.app.GlobalMockData.userLocation.value
        val farmArea = "${com.krushiadhaar.app.GlobalMockData.farmerFarmArea.value} ${com.krushiadhaar.app.GlobalMockData.farmerAreaUnit.value}"
        val lat = com.krushiadhaar.app.GlobalMockData.farmerLatitude.value
        val lon = com.krushiadhaar.app.GlobalMockData.farmerLongitude.value
        val primaryCrop = com.krushiadhaar.app.GlobalMockData.farmerCrop.value

        canvas.drawText("Farmer Name: $farmerName", 35f, y, textPaint)
        canvas.drawText("Location: $farmerLoc", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Farm Area: $farmArea (Cultivated)", 35f, y, textPaint)
        canvas.drawText("Coordinates: ${"%.4f".format(lat)}° N, ${"%.4f".format(lon)}° E", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Soil Type: Optimal Fertile Loam", 35f, y, textPaint)
        canvas.drawText("Irrigation Source: Drip & Borewell", 300f, y, textPaint)

        y += 30f

        // 2. Seasonal Production & Crop Yield
        canvas.drawText("2. CURRENT SEASON CROP PRODUCTION", 25f, y, boldPaint)
        y += 6f
        canvas.drawLine(25f, y, 570f, y, linePaint)
        y += 18f

        canvas.drawText("Primary Crop: $primaryCrop", 35f, y, textPaint)
        canvas.drawText("Sowing Date: 06 October 2026", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Growth Stage: Vegetative Stage", 35f, y, textPaint)
        canvas.drawText("Expected Yield: 18.5 Tons", 300f, y, textPaint)

        y += 30f

        // 4. Financial Ledger Summary
        canvas.drawText("3. FINANCIAL LEDGER & WALLET SUMMARY", 25f, y, boldPaint)
        y += 6f
        canvas.drawLine(25f, y, 570f, y, linePaint)
        y += 18f

        val wallet = GlobalMockData.walletBalance.value
        val rev = GlobalMockData.cropYieldRevenue.value
        canvas.drawText("Current Wallet Balance: Rs. $wallet", 35f, y, textPaint)
        canvas.drawText("Season Crop Yield Revenue: Rs. $rev", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Estimated Input Expenses: Rs. 16,800", 35f, y, textPaint)
        val profit = if (rev > 0) rev - 16800 else wallet - 16800
        canvas.drawText("Net Farm Profit (Est.): Rs. $profit", 300f, y, boldPaint)

        y += 30f

        // 5. Soil Health Card Diagnostics
        canvas.drawText("4. SOIL HEALTH CARD DIAGNOSTICS (NPK & pH)", 25f, y, boldPaint)
        y += 6f
        canvas.drawLine(25f, y, 570f, y, linePaint)
        y += 18f

        canvas.drawText("Nitrogen (N): 210 kg/ha  [Status: Low Deficit]", 35f, y, textPaint)
        canvas.drawText("Prescribed Urea: 2.1 Bags/Acre", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Phosphorus (P): 18 kg/ha [Status: Medium Optimal]", 35f, y, textPaint)
        canvas.drawText("Prescribed DAP: 1.1 Bags/Acre", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Potassium (K): 220 kg/ha  [Status: Medium Optimal]", 35f, y, textPaint)
        canvas.drawText("Prescribed MOP: 0.7 Bags/Acre", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Soil pH: 7.2 [Status: Neutral / Ideal for Nutrient Uptake]", 35f, y, textPaint)

        y += 30f

        // 6. AI Disease Detection & Scouting History
        canvas.drawText("5. AI DISEASE DIAGNOSIS & HEALTH AUDIT", 25f, y, boldPaint)
        y += 6f
        canvas.drawLine(25f, y, 570f, y, linePaint)
        y += 18f

        canvas.drawText("Latest AI Leaf Scan: Passed (Confidence: 98%)", 35f, y, textPaint)
        canvas.drawText("Diagnostic Classification: Leaf Blight (Mild)", 300f, y, textPaint)
        y += 16f
        canvas.drawText("Prescribed Treatment: Mancozeb (2g/L) + Neem Oil Spray", 35f, y, textPaint)
        canvas.drawText("Treatment Cost: ~Rs. 1,200/Acre", 300f, y, textPaint)

        y += 40f

        // Footer / Stamp
        canvas.drawLine(25f, y, 570f, y, linePaint)
        y += 20f
        canvas.drawText("Certified by Krishi-Aadhar Intelligence System", 160f, y, subPaint)

        pdfDocument.finishPage(page)

        // Save PDF to app storage
        return try {
            val fileDir = File(context.getExternalFilesDir(null), "reports")
            if (!fileDir.exists()) fileDir.mkdirs()
            val file = File(fileDir, "Krishi_Aadhar_Farm_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.flush()
            outputStream.close()

            // Trigger Share Intent
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Krishi-Aadhar Farm Health & Revenue Report")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Farm Report PDF"))

            Toast.makeText(context, "Report generated successfully!", Toast.LENGTH_SHORT).show()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            e.printStackTrace()
            Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }
}
