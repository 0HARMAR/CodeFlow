export const UserAction = Object.freeze({
    IMPRESSION: Object.freeze({
        level: 0,
        weight: 0.0,
        positive: false,
    }),

    CLICK: Object.freeze({
        level: 1,
        weight: 1.0,
        positive: true,
    }),

    READ: Object.freeze({
        level: 2,
        weight: 2.0,
        positive: true,
    }),

    FAVORITE: Object.freeze({
        level: 3,
        weight: 3.0,
        positive: true,
    }),
})
