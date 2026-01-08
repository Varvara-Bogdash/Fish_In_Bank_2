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
    private Paint titlePaint;
    private int[] colors = {
            Color.rgb(220, 100, 120),  // Темный розовый
            Color.rgb(100, 120, 220),  // Темный голубой
            Color.rgb(100, 180, 100),  // Темный зеленый
            Color.rgb(220, 180, 100),  // Темный персиковый
            Color.rgb(180, 100, 180),  // Темный фиолетовый
            Color.rgb(100, 180, 220),  // Темный бирюзовый
            Color.rgb(220, 120, 130),  // Темный розовый 2
            Color.rgb(120, 200, 120),  // Темный салатовый
            Color.rgb(220, 160, 110),  // Темный абрикосовый
            Color.rgb(160, 110, 180),  // Темный лавандовый
            Color.rgb(140, 160, 200),  // Серо-голубой
            Color.rgb(200, 140, 160),  // Розовато-коричневый
            Color.rgb(140, 200, 180),  // Мятный
            Color.rgb(200, 180, 140),  // Бежевый
            Color.rgb(160, 140, 200),  // Сиреневый
            Color.rgb(200, 140, 200),  // Пурпурный
            Color.rgb(140, 200, 140),  // Светло-зеленый
            Color.rgb(200, 200, 140),  // Светло-желтый
            Color.rgb(140, 160, 180),  // Стальной
            Color.rgb(180, 140, 160)   // Розово-серый
    };
    private DecimalFormat df = new DecimalFormat("#.#");
    private float scaleFactor = 1.0f; // Коэффициент масштабирования для адаптации
    private float chartSize = 0; // Храним размер диаграммы

    // Поля для хранения данных диаграммы
    private float[] values = new float[0];
    private String[] categoryNames = new String[0];
    private boolean useExternalData = false; // Флаг использования внешних данных

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
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        legendPaint = new Paint();
        legendPaint.setAntiAlias(true);
        legendPaint.setColor(Color.YELLOW);
        legendPaint.setTextSize(40f);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setColor(Color.YELLOW);
        titlePaint.setTextSize(40f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рассчитываем размер диаграммы один раз, чтобы он был одинаковым
        chartSize = Math.min(getWidth(), getHeight()) * 0.55f;

        // Выбираем источник данных в зависимости от флага
        boolean isEmpty;
        double totalExpenses;
        Map<String, Double> dataMap;

        if (useExternalData && values.length > 0) {
            // Используем внешние данные
            isEmpty = values.length == 0;
            totalExpenses = calculateTotal(values);
            dataMap = convertToMap();
        } else {
            // Используем данные из DataManager
            if (DataManager.expensesByCategory == null || DataManager.expensesByCategory.isEmpty()) {
                drawEmptyChart(canvas);
                return;
            }
            isEmpty = DataManager.expensesByCategory.isEmpty();
            totalExpenses = DataManager.totalExpense;
            dataMap = DataManager.expensesByCategory;
        }

        if (isEmpty || totalExpenses == 0.0) {
            drawEmptyChart(canvas);
            return;
        }

        // Рассчитываем масштабный коэффициент на основе размера легенды
        calculateScaleFactor(dataMap);

        // Применяем масштабирование к размерам шрифтов
        float currentLegendTextSize = 40f * scaleFactor;
        legendPaint.setTextSize(currentLegendTextSize);

        // Рисуем легенду слева и получаем ее ширину
        float legendWidth = drawLegendLeft(canvas, totalExpenses, dataMap);

        // Используем фиксированный размер диаграммы (такой же как в пустой)
        float centerX = legendWidth + (getWidth() - legendWidth) / 2f + 70f; // Смещение диаграммы вправо
        float centerY = getHeight() / 3f; // Смещаем диаграмму ниже

        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );

        // Рисуем сегменты диаграммы
        drawPieChart(canvas, totalExpenses, rect, centerX, centerY, chartSize, dataMap);

        // Заголовок диаграммы
        canvas.drawText("Расходы по категориям", getWidth() / 2f, getHeight() - 80f, titlePaint);
    }

    /**
     * Устанавливает данные для диаграммы из массива значений
     */
    public void setData(float[] values) {
        if (values == null) {
            this.values = new float[0];
        } else {
            this.values = values.clone();
        }
        useExternalData = true;
        invalidate();
    }

    /**
     * Устанавливает названия категорий для диаграммы
     */
    public void setCategoryNames(String[] categoryNames) {
        if (categoryNames == null) {
            this.categoryNames = new String[0];
        } else {
            this.categoryNames = categoryNames.clone();
        }
        invalidate();
    }

    /**
     * Возвращает текущие данные диаграммы
     */
    public float[] getData() {
        return values.clone();
    }

    /**
     * Возвращает текущие названия категорий
     */
    public String[] getCategoryNames() {
        return categoryNames.clone();
    }

    /**
     * Очищает данные диаграммы
     */
    public void clearData() {
        values = new float[0];
        categoryNames = new String[0];
        useExternalData = false;
        invalidate();
    }

    /**
     * Восстанавливает использование данных из DataManager
     */
    public void useDataManager() {
        useExternalData = false;
        invalidate();
    }

    /**
     * Пересчитывает коэффициент масштабирования на основе данных
     */
    private void calculateScaleFactor(Map<String, Double> dataMap) {
        if (dataMap == null) return;

        int itemCount = dataMap.size();
        float maxHeight = getHeight() * 0.8f; // Максимальная доступная высота
        float lineHeight = 55f; // Базовая высота строки

        // Рассчитываем необходимую высоту для легенды
        float neededHeight = itemCount * lineHeight;

        // Если легенда слишком большая, уменьшаем масштаб
        if (neededHeight > maxHeight) {
            scaleFactor = maxHeight / neededHeight;
            // Ограничиваем минимальный масштаб
            scaleFactor = Math.max(scaleFactor, 0.7f);
        } else {
            scaleFactor = 1.0f;
        }

        // Также проверяем ширину
        float maxWidth = getWidth() * 0.4f; // Максимальная ширина для легенды
        if (maxWidth < getWidth() * 0.3f) {
            scaleFactor = Math.min(scaleFactor, 0.8f);
        }
    }

    /**
     * Рисует легенду слева
     */
    private float drawLegendLeft(Canvas canvas, double totalExpenses, Map<String, Double> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return 0;
        }

        float legendX = 20f;
        float legendY = 40f;
        float rectSize = 35f * scaleFactor;
        float textOffset = 50f * scaleFactor;
        float lineHeight = 55f * scaleFactor;

        // Рассчитываем максимальную ширину текста в легенде
        float maxTextWidth = 0;
        float currentLegendTextSize = legendPaint.getTextSize();

        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalExpenses) * 100;
            String legendText = category + " (" + df.format(percentage) + "%)";
            float textWidth = legendPaint.measureText(legendText);
            maxTextWidth = Math.max(maxTextWidth, textWidth);
        }

        // Ширина легенды = отступ + размер прямоугольника + отступ текста + ширина текста
        float legendWidth = legendX + rectSize + textOffset + maxTextWidth + 40f;

        // Увеличиваем доступную ширину для текста до 45% ширины экрана
        float maxAllowedLegendWidth = getWidth() * 0.45f;
        if (legendWidth > maxAllowedLegendWidth) {
            // Если легенда слишком широкая, уменьшаем масштаб для ширины
            float widthScale = maxAllowedLegendWidth / legendWidth;
            scaleFactor = Math.min(scaleFactor, widthScale);

            currentLegendTextSize = 40f * scaleFactor;
            legendPaint.setTextSize(currentLegendTextSize);
            rectSize = 35f * scaleFactor;
            textOffset = 50f * scaleFactor;
            lineHeight = 55f * scaleFactor;
            legendWidth = legendX + rectSize + textOffset + maxTextWidth * scaleFactor + 40f;
        }

        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalExpenses) * 100;

            // Цветной прямоугольник
            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawRect(legendX, legendY, legendX + rectSize, legendY + rectSize, paint);

            // Текст категории и процента - полный текст
            String legendText = category + " (" + df.format(percentage) + "%)";

            // отображается полное название
            canvas.drawText(legendText, legendX + textOffset,
                    legendY + rectSize/2 + currentLegendTextSize/3, legendPaint);

            legendY += lineHeight;
            colorIndex++;
        }

        return legendWidth;
    }

    /**
     * Рисует круговую диаграмму
     */
    private void drawPieChart(Canvas canvas, double totalExpenses, RectF rect,
                              float centerX, float centerY, float chartSize, Map<String, Double> dataMap) {
        float startAngle = 0f;
        int colorIndex = 0;

        // Рисуем сегменты диаграммы
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
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
    }

    /**
     * Сокращает текст если он слишком длинный
     */
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

    /**
     * Рисует пустую диаграмму
     */
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
        Paint emptyTextPaint = new Paint();
        emptyTextPaint.setAntiAlias(true);
        emptyTextPaint.setColor(Color.YELLOW);
        emptyTextPaint.setTextSize(30f);
        emptyTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Нет данных о расходах", centerX, centerY, emptyTextPaint);

        // Заголовок
        canvas.drawText("Расходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }

    /**
     * Рассчитывает общую сумму из массива значений
     */
    private double calculateTotal(float[] values) {
        double total = 0.0;
        for (float value : values) {
            total += value;
        }
        return total;
    }

    /**
     * Конвертирует массив значений и названий категорий в Map
     */
    private Map<String, Double> convertToMap() {
        java.util.HashMap<String, Double> map = new java.util.HashMap<>();

        if (values.length == 0) {
            return map;
        }

        for (int i = 0; i < values.length; i++) {
            String categoryName;
            if (categoryNames.length > i && categoryNames[i] != null && !categoryNames[i].isEmpty()) {
                categoryName = categoryNames[i];
            } else {
                categoryName = "Категория " + (i + 1);
            }
            map.put(categoryName, (double) values[i]);
        }

        return map;
    }
}