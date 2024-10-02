import React, {useEffect, useState} from 'react';
import {BookModel} from "../../models/BookModel";
import {API_URL} from "../../constants";
import {SpinnerLoading} from "../Utils/SpinnerLoading";
import {StarsReview} from "../Utils/StarsReview";
import {CheckoutAndReview} from "./CheckoutAndReview";
import {ReviewModel} from "../../models/ReviewModel";
import {LatestReviews} from "./LatestReviews";


export const BookCheckoutPage = () => {

        const [book, setBook] = useState<BookModel>();
        const [isLoading, setIsLoading] = useState(true);
        const [httpError, setHttpError] = useState(null);

        // Review State
        const [reviews, setReviews] = useState<ReviewModel[]>([]);
        const [totalStars, setTotalStars] = useState(0);
        const [isLoadingReviews, setIsLoadingReviews] = useState(true);
        const bookId = (window.location.pathname).split('/')[2];

        useEffect(() => {

            const fetchBook = async () => {

                const url = API_URL + `/books/${bookId}`;
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error('Something went wrong.');
                }
                const responseJson = await response.json();
                const loadedBook: BookModel = {
                    id: responseJson.id,
                    title: responseJson.title,
                    author: responseJson.author,
                    description: responseJson.description,
                    copies: responseJson.copies,
                    copiesAvailable: responseJson.copiesAvailable,
                    category: responseJson.category,
                    img: responseJson.img
                }
                setBook(loadedBook);
                setIsLoading(false);
            }
            fetchBook().catch((error: any) => {
                setIsLoading(false);
                setHttpError(error.message);
            });
        }, [])

        useEffect(() => {
            const fetchBookReivews = async () => {
                const url = API_URL + `/reviews/search/findByBookId?bookId=${bookId}&page=0&size=5';`
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error("Something went wrong.");
                }

                const responseJson = await response.json();
                const responseReviews: ReviewModel[] = responseJson._embedded.reviews;
                let weightedStarReviews = 0;
                const loadedReviews = responseReviews.map((review) => {
                    weightedStarReviews += review.rating;
                    return new ReviewModel(
                        review.id,
                        review.userEmail,
                        review.date,
                        review.rating,
                        review.book_id,
                        review.reviewDescription
                    );
                });
                if (loadedReviews) {
                    const round = (Math.round(weightedStarReviews / loadedReviews.length) * 2 / 2).toFixed(1);
                    setTotalStars(Number(round));
                }
                setReviews(loadedReviews);
                setIsLoadingReviews(false)
            }
            fetchBookReivews().catch((error: any) => {
                setIsLoadingReviews(false)
                setHttpError(error.message);
            });

        }, []);


        if (isLoading || isLoadingReviews) {
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
            <div>
                <div className='container d-none d-lg-block'>
                    <div className='row mt-5'>
                        <div className='col-sm-2 col-md-2'>
                            {book?.img ?
                                <img src={book.img} width='226' height='349' alt='Book'/>
                                :
                                <img src={require('./../../Images/BooksImages/book-luv2code-1000.png')} width='226'
                                     height='349' alt='Book'/>
                            }
                        </div>
                        <div className='col-4 col-md-4 container'>
                            <div className='ml-2'>
                                <h2>{book?.title}</h2>
                                <h5 className='text-primary'>{book?.author}</h5>
                                <p className='lead'>{book?.description}</p>
                                <StarsReview rating={totalStars} size={32}/>
                            </div>
                        </div>
                        <CheckoutAndReview book={book} mobile={false}/>
                    </div>
                    <hr/>
                    <LatestReviews reviews={reviews} bookId={book?.id} mobile={false}/>
                </div>
                <div className='container d-lg-none mt-5'>
                    <div className='d-flex justify-content-center align-items-center'>
                        {book?.img ?
                            <img src={book.img} width='226' height='349' alt='Book'/>
                            :
                            <img src={require('./../../Images/BooksImages/book-luv2code-1000.png')} width='226'
                                 height='349' alt='Book'/>
                        }
                    </div>
                    <div className='mt-4'>
                        <div className='ml-2'>
                            <h2>{book?.title}</h2>
                            <h5 className='text-primary'>{book?.author}</h5>
                            <p className='lead'>{book?.description}</p>
                        </div>
                    </div>
                    <CheckoutAndReview book={book} mobile={true}/>
                    <hr/>
                    <LatestReviews reviews={reviews} bookId={book?.id} mobile={true}/>
                </div>
            </div>
        );
    }
;
