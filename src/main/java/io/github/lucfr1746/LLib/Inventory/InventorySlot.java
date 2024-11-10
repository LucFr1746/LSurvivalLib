package io.github.lucfr1746.LLib.Inventory;

import java.util.List;

public enum InventorySlot {
    FIRST_ROW(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)),
    SECOND_ROW(List.of(9, 10, 11, 12, 13, 14, 15, 16, 17)),
    THIRD_ROW(List.of(18, 19, 20, 21, 22, 23, 24, 25, 26)),
    FOURTH_ROW(List.of(27, 28, 29, 30, 31, 32, 33, 34, 35)),
    FIFTH_ROW(List.of(36, 37, 38, 39, 40, 41, 42, 43, 44)),
    SIXTH_ROW(List.of(45, 46, 47, 48, 49, 50, 51, 52, 53)),

    FIRST_COLUMN(List.of(0, 9, 18, 27, 36, 45)),
    SECOND_COLUMN(List.of(1, 10, 19, 28, 37, 46)),
    THIRD_COLUMN(List.of(2, 11, 20, 29, 38, 47)),
    FOURTH_COLUMN(List.of(3, 12, 21, 30, 39, 48)),
    FIFTH_COLUMN(List.of(4, 13, 22, 31, 40, 49)),
    SIXTH_COLUMN(List.of(5, 14, 23, 32, 41, 50)),
    SEVENTH_COLUMN(List.of(6, 15, 24, 33, 42, 51)),
    EIGHTH_COLUMN(List.of(7, 16, 25, 34, 43, 52)),
    NINTH_COLUMN(List.of(8, 17, 26, 35, 44, 53)),

    ODD_INTERLEAVED_SLOTS(List.of(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52)),
    EVEN_INTERLEAVED_SLOTS(List.of(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43, 45, 47, 49, 51, 53)),

    ODD_INTERLEAVED_COLUMN(List.of(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26, 27, 29, 31, 33, 35, 36, 38, 40, 42, 44, 45, 47, 49, 51, 53)),
    EVEN_INTERLEAVED_COLUMN(List.of(1, 3, 5, 7, 10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32, 34, 37, 39, 41, 43, 46, 48, 50, 52)),

    BORDER_OF_27(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26)),
    BORDER_OF_36(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35)),
    BORDER_OF_45(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44)),
    BORDER_OF_54(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53)),

    CENTER_SLOT_OF_9(List.of(4)),
    CENTER_SLOT_OF_18(List.of(13)),
    CENTER_SLOT_OF_27(List.of(22)),
    CENTER_SLOT_OF_36(List.of(31)),
    CENTER_SLOT_OF_45(List.of(40)),
    CENTER_SLOT_OF_54(List.of(49));

    private final List<Integer> slots;

    public List<Integer> getSlots() {
        return this.slots;
    }

    InventorySlot(List<Integer> slots) {
        this.slots = slots;
    }
}
