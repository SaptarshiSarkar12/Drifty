package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Unit Converter")
class UnitConverterTest {
    @Test
    @DisplayName("Test Data Size Conversion")
    void testDataSizeConversion() {
        Assertions.assertEquals(10, UnitConverter.getValue(10, UnitConverter.B));
        Assertions.assertEquals(10, UnitConverter.getValue(10240, UnitConverter.KB));
        Assertions.assertEquals(10, UnitConverter.getValue(10485760, UnitConverter.MB));
        Assertions.assertEquals(10, UnitConverter.getValue(10737418240L, UnitConverter.GB));
        Assertions.assertEquals(10, UnitConverter.getValue(10995116277760L, UnitConverter.TB));
    }

    @Test
    @DisplayName("Test Data Size Unit")
    void testDataSizeUnit() {
        Assertions.assertEquals("B", UnitConverter.format(10, 1).split(" ")[1]);
        Assertions.assertEquals("KB", UnitConverter.format(10240, 1).split(" ")[1]);
        Assertions.assertEquals("MB", UnitConverter.format(10485760, 1).split(" ")[1]);
        Assertions.assertEquals("GB", UnitConverter.format(10737418240L, 1).split(" ")[1]);
        Assertions.assertEquals("TB", UnitConverter.format(10995116277760L, 1).split(" ")[1]);
    }

    @Test
    @DisplayName("Test Data Size Formatting")
    void testDataSizeFormatting() {
        Assertions.assertEquals("10.0 B", UnitConverter.format(10, 1));
        Assertions.assertEquals("10.0 KB", UnitConverter.format(10240, 1));
        Assertions.assertEquals("10.0 MB", UnitConverter.format(10485760, 1));
        Assertions.assertEquals("10.0 GB", UnitConverter.format(10737418240L, 1));
        Assertions.assertEquals("10.0 TB", UnitConverter.format(10995116277760L, 1));
    }

    @Test
    @DisplayName("Test Data Size Formatting with Decimal Places")
    void testDataSizeFormattingWithDecimalPlaces() {
        Assertions.assertEquals("10.00 B", UnitConverter.format(10, 2));
        Assertions.assertEquals("10.00 KB", UnitConverter.format(10240, 2));
        Assertions.assertEquals("10.00 MB", UnitConverter.format(10485760, 2));
        Assertions.assertEquals("10.00 GB", UnitConverter.format(10737418240L, 2));
        Assertions.assertEquals("10.00 TB", UnitConverter.format(10995116277760L, 2));
    }
}