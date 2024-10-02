import React, {useEffect, useState} from 'react';
import {BookModel} from "../../models/BookModel";
import {SpinnerLoading} from "../Utils/SpinnerLoading";
import {SearchBook} from "./components/SearchBook";
import {Pagination} from "../Utils/Pagination";

export const SearchBooksPage = () => {
    const booksUrl = "http://localhost:8080/api/books";

    const [books, setBooks] = useState<BookModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [booksPerPage, setBooksPerPage] = useState(5);
    const [totalBooks, setTotalBooks] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [search, setSearch] = useState('');
    const [searchUrl, setSearchUrl] = useState('');
    const [categorySelection, setCategorySelection] = useState('Book Category');

    useEffect(() => {

        const fetchBooks = async () => {
            //const booksUrl = "http://35.196.186.50/api/books";
            let url = '';
            if (searchUrl === '') {
                url = booksUrl + `?page=${currentPage - 1}&size=${booksPerPage}`;
            } else {
                let searchWithPage = searchUrl.replace('<pageNumber', `${currentPage - 1}`);
                url = booksUrl + searchWithPage;
            }

            const response = await fetch(url);
            if (!response.ok) {
                throw new Error("Something went wrong!");
            }

            const responseJson = await response.json();
            setTotalBooks(responseJson.page.totalElements);
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

        window.scrollTo(0, 0);

    }, [currentPage, searchUrl]);

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

    const searchHandleChange = () => {
        setCurrentPage(1);
        if (search === '') {
            setSearchUrl('');
        } else {
            setSearchUrl(`/search/findByTitleContaining?title=${search}&page=<pageNumber>&size=${booksPerPage}`);
        }
        setCategorySelection('Book category')
    }

    const categoryField = (value: string) => {
        setCurrentPage(1);
        switch (value.toLowerCase()) {

            case 'fe':
            case 'be':
            case 'data':
            case 'devops':
                setCategorySelection(value);
                setSearchUrl(`/search/findByCategory?category=${value}&page=<pageNumber>&size=${booksPerPage}`);
                break;
            default:
                setCategorySelection('All');
                setSearchUrl(`?page=<pageNumber>&size=${booksPerPage}`);
        }
    }

    const indexOfLastBook: number = currentPage * booksPerPage;
    const indexOfFirstBook: number = indexOfLastBook - booksPerPage;
    let lastItem = Math.min(booksPerPage * currentPage, totalBooks);
    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return (
        <div>
            <div className='container'>
                <div>
                    <div className='row mt-5'>
                        <div className='col-6'>
                            <div className='d-flex'>
                                <input className='form-control me-2' type='search'
                                       placeholder='Search' aria-labelledby='Search'
                                       onChange={e => setSearch(e.target.value)}
                                />
                                <button className='btn btn-outline-success'
                                        onClick={searchHandleChange}>
                                    Search
                                </button>
                            </div>
                        </div>
                        <div className='col-4'>
                            <div className='dropdown'>
                                <button className='btn btn-secondary dropdown-toggle' type='button'
                                        id='dropdownMenuButton1' data-bs-toggle='dropdown'
                                        aria-expanded='false'>
                                    {categorySelection}
                                </button>
                                <ul className='dropdown-menu' aria-labelledby='dropdownMenuButton1'>
                                    <li onClick={() => categoryField('All')}>
                                        <a className='dropdown-item' href='#'>
                                            All
                                        </a>
                                    </li>
                                    <li onClick={() => categoryField('fe')}>
                                        <a className='dropdown-item' href='#'>
                                            Front End
                                        </a>
                                    </li>
                                    <li onClick={() => categoryField('be')}>
                                        <a className='dropdown-item' href='#'>
                                            Back End
                                        </a>
                                    </li>
                                    <li onClick={() => categoryField('data')}>
                                        <a className='dropdown-item' href='#'>
                                            Data
                                        </a>
                                    </li>
                                    <li onClick={() => categoryField('devops')}>
                                        <a className='dropdown-item' href='#'>
                                            DevOps
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    {totalBooks > 0 ?
                        <>
                            <div className='mt-3'>
                                <h5>Number of results: ({totalBooks})</h5>
                            </div>
                            <p>
                                {indexOfFirstBook + 1} to {lastItem} of {totalBooks} items:
                            </p>

                            {books.map(book => (
                                <SearchBook book={book} key={book.id}/>
                            ))}
                        </>
                        :
                        <div className='mt-5'>
                            <h3>
                                Can't find what you're looking for?
                            </h3>
                            <a type='button' className='btn main-color btn-md px-4 me-md-2 fw-bold text-white' href='#'>
                                Library Services
                            </a>
                        </div>
                    }
                    {totalPages > 1 &&
                        <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate}/>
                    }

                </div>
            </div>
        </div>
    )
        ;
}//