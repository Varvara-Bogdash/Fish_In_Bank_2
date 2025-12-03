package com.example.fishinbank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.text.DecimalFormat;
import java.util.Map;

public class PieChartView extends View {
    private Paint paint;
    private Paint textPaint;
    private Paint legendPaint;
    private int[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.CYAN, Color.MAGENTA, Color.GRAY, Color.DKGRAY,
            Color.rgb(255, 165, 0), Color.rgb(128, 0, 128)
    };
    private DecimalFormat df = new DecimalFormat("#.#");

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(14f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        legendPaint = new Paint();
        legendPaint.setAntiAlias(true);
        legendPaint.setColor(Color.WHITE);
        legendPaint.setTextSize(12f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (DataManager.expensesByCategory == null || DataManager.expensesByCategory.isEmpty()) {
            drawEmptyChart(canvas);
            return;
        }

        double totalExpenses = 0.0;
        for (double value : DataManager.expensesByCategory.values()) {
            totalExpenses += value;
        }

        if (totalExpenses == 0.0) {
            drawEmptyChart(canvas);
            return;
        }

        // Рисуем диаграмму (уменьшенный размер для места под легенду)
        float startAngle = 0f;
        int colorIndex = 0;
        float chartSize = Math.min(getWidth(), getHeight()) * 0.6f; // Уменьшаем размер диаграммы
        float padding = 10f;
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 3f; // Смещаем диаграмму вверх для легенды

        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );

        // Рисуем сегменты диаграммы
        for (Map.Entry<String, Double> entry : DataManager.expensesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            float sweepAngle = (float) (value / totalExpenses * 360);
            double percentage = (value / totalExpenses) * 100;

            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

            // Добавляем процент в центр сегмента (если сегмент достаточно большой)
            if (sweepAngle > 20f) {
                float middleAngle = startAngle + sweepAngle / 2;
                float textRadius = chartSize * 0.3f;
                float textAngle = (float) Math.toRadians(middleAngle);

                float textX = centerX + (float) (textRadius * Math.cos(textAngle));
                float textY = centerY + (float) (textRadius * Math.sin(textAngle));

                String percentText = df.format(percentage) + "%";
                canvas.drawText(percentText, textX, textY, textPaint);
            }

            startAngle += sweepAngle;
            colorIndex++;
        }

        // Рисуем легенду
        drawLegend(canvas, totalExpenses, centerY + chartSize/2 + 20f);

        // Заголовок диаграммы
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(18f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Расходы по категориям", centerX, 30f, titlePaint);
    }

    private void drawLegend(Canvas canvas, double totalExpenses, float startY) {
        float legendX = 20f;
        float legendY = startY;
        float rectSize = 20f;
        float textOffset = 30f;
        float lineHeight = 25f;
        int maxWidth = getWidth() - 40;

        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : DataManager.expensesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalExpenses) * 100;

            // Проверяем, не вышли ли за пределы экрана
            if (legendY + lineHeight > getHeight() - 20f) {
                break; // Не помещаемся - останавливаемся
            }

            // Цветной прямоугольник
            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawRect(legendX, legendY, legendX + rectSize, legendY + rectSize, paint);

            // Текст категории и процента
            String legendText = category + " (" + df.format(percentage) + "%)";

            // Обрезаем текст если слишком длинный
            if (legendPaint.measureText(legendText) > maxWidth - legendX - textOffset) {
                legendText = shortenText(legendPaint, legendText, maxWidth - legendX - textOffset);
            }

            canvas.drawText(legendText, legendX + textOffset, legendY + 15f, legendPaint);

            legendY += lineHeight;
            colorIndex++;
        }
    }

    private String shortenText(Paint paint, String text, float maxWidth) {
        if (paint.measureText(text) <= maxWidth) {
            return text;
        }

        int endIndex = text.length() - 1;
        while (endIndex > 3 && paint.measureText(text.substring(0, endIndex) + "...") > maxWidth) {
            endIndex--;
        }

        return text.substring(0, endIndex) + "...";
    }

    private void drawEmptyChart(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 3f;
        float chartSize = Math.min(getWidth(), getHeight()) * 0.6f;

        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );
        canvas.drawArc(rect, 0f, 360f, false, paint);
        paint.setStyle(Paint.Style.FILL);

        // Сообщение об отсутствии данных
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Нет данных о расходах", centerX, centerY, textPaint);

        // Заголовок
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(18f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Расходы по категориям", centerX, 30f, titlePaint);
    }
}