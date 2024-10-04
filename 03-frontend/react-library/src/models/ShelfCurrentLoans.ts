import {BookModel} from "./BookModel";

export class ShelfCurrentLoans {

    constructor(
        public book: BookModel,
        public daysLeft: number
    ) {
    }
}