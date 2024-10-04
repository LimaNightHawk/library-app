import {API_URL} from "../../../constants";
import React, {useEffect, useState} from "react";
import {BookModel} from "../../../models/BookModel";
import {SpinnerLoading} from "../../Utils/SpinnerLoading";
import {Pagination} from "../../Utils/Pagination";
import {ChangeQuantityOfBook} from "./ChangeQuantityOfBook";

export const ChangeQuantityOfBooks:React.FC<{}> = () => {


    const [books, setBooks] = useState<BookModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [booksPerPage, setBooksPerPage] = useState(5);
    const [totalAmountOfBooks, setTotalAmountOfBooks] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [bookDelete, setBookDelete] = useState(false);


    useEffect(() => {

        const fetchBooks = async () => {
            const baseUrl = API_URL + `/books?page=${currentPage - 1}&size=${booksPerPage}`;

            const response = await fetch(baseUrl);
            if (!response.ok) {
                throw new Error("Something went wrong!");
            }

            const responseJson = await response.json();
            setTotalAmountOfBooks(responseJson.page.totalElements);
            setTotalPages(responseJson.page.totalPages);
            const responseBooks: BookModel[] = responseJson._embedded.books;
            const loadedBooks: BookModel[] = responseBooks.map((book: BookModel) => {
                return new BookModel(
                    book.id,
                    book.title,
                    book.author,
                    book.description,
                    book.copies,
                    book.copiesAvailable,
                    book.category,
                    book.img
                );
            });
            setBooks(loadedBooks);
            setIsLoading(false);
        };
        fetchBooks().catch((error: any) => {
            setIsLoading(false);
            setHttpError(error.message);
        });

    }, [currentPage, bookDelete]);

    const indexOfLastBook: number = currentPage * booksPerPage;
    const indexOfFirstBook: number = indexOfLastBook - booksPerPage;
    let lastItem = Math.min(booksPerPage * currentPage, totalAmountOfBooks);
    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    const deletebook = () => {
        setBookDelete(!bookDelete);
    }

    if (isLoading) {
        return (
            <SpinnerLoading/>
        );
    }
    if (httpError) {
        return (
            <div className='container mt-5'>
                <p>{httpError}</p>
            </div>
        );
    }

    return (
        <div className='container mt-5'>
            {totalAmountOfBooks > 0 ?
                <>
                    <div className='mt-3'>
                        <h3>Number of results: ({totalAmountOfBooks})</h3>
                    </div>
                    <p>
                        {indexOfFirstBook + 1} to {lastItem} of {totalAmountOfBooks} items:
                    </p>
                    {books.map(book => (
                        <ChangeQuantityOfBook book={book} key={book.id} deleteBook={deletebook}/>
                    ))}
                </>
                :
                <h5>Add a book before changing quantity</h5>

            }
            {totalPages > 1 && <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate}/>}
        </div>
    )
}