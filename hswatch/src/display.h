#ifndef DISPLAY_H
#define DISPLAY_H

typedef enum align_text {left, center, right, both} align_text;
typedef enum font_type {arial_10, arial_16, arial_24} font_type;

class Display{

    public:

    //simple
    static void clear(void);
    static void display(void);
    static void setPixel(int16_t x, int16_t y);
    static void drawLine(int16_t x0, int16_t y0, int16_t x1, int16_t y1);
    static void drawRect(int16_t x, int16_t y, int16_t width, int16_t height);
    static void fillRect(int16_t x, int16_t y, int16_t width, int16_t height);
    static void drawCircle(int16_t x, int16_t y, int16_t radius);
    static void fillCircle(int16_t x, int16_t y, int16_t radius);
    static void drawHorizontalLine(int16_t x, int16_t y, int16_t length);
    static void drawVerticalLine(int16_t x, int16_t y, int16_t length);
    static void drawProgressBar(uint16_t x, uint16_t y, uint16_t width, uint16_t height, uint8_t progress);
    static void drawFastImage(int16_t x, int16_t y, int16_t width, int16_t height, const uint8_t *image);
    static void drawXbm(int16_t x, int16_t y, int16_t width, int16_t height, const char* xbm);
    static void drawString(int16_t x, int16_t y, String text);
    static void drawStringMaxWidth(int16_t x, int16_t y, int16_t maxLineWidth, String text);
    static uint16_t getStringWidth(String text);
    static void setTextAlignment(align_text textAlignment);
    static void setFont(font_type fontData);

    //complex
    //static void default_display(String);

};

#endif

