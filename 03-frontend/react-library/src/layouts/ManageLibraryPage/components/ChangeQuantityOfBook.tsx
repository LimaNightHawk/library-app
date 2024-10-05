import React, {useEffect, useState} from 'react';
import {useOktaAuth} from "@okta/okta-react";
import {SpinnerLoading} from "../../Utils/SpinnerLoading";
import {BookModel} from "../../../models/BookModel";


export const ChangeQuantityOfBook: React.FC<{ book: BookModel, deleteBook: any }> = (props, key) => {

    const {authState} = useOktaAuth();
    const [quantity, setQuantity] = useState(0);
    const [remaining, setRemaining] = useState(0);
    const [httpError, setHttpError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        const fetchBooksInState = () => {
            setQuantity(props.book.copies || 0);
            setRemaining(props.book.copiesAvailable || 0);
        }
        fetchBooksInState();
    }, []);

    async function changeQuantity(increase: number) {
        const newQuantity = quantity + increase;
        const newRemaining = remaining + increase;
        if (authState && authState.isAuthenticated && newQuantity >= 0 && newRemaining >= 0) {

            const url = `${process.env.REACT_APP_API}/admin/secure/book/quantity?bookId=${props.book.id}&quantity=${increase}`;
            const requestOptions = {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                    'Content-Type': 'application/json'
                }
            }
            const response = await fetch(url, requestOptions);
            if (!response.ok) {
                throw new Error("Error trying to fetch ");
            }
            setQuantity(newQuantity);
            setRemaining(newRemaining);
        }
    }

    async function deleteBook() {
        if (authState && authState.isAuthenticated) {

            const url = `${process.env.REACT_APP_API}/admin/secure/delete/book?bookId=${props.book.id}`;
            const requestOptions = {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                    'Content-Type': 'application/json'
                }
            }
            const response = await fetch(url, requestOptions);
            if (!response.ok) {
                throw new Error("Error trying to fetch ");
            }
            props.deleteBook();
        }
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
        <div className='card mt-3 shadow p-3 mb-3 bg-body rounded'>
            <div className='row g-0'>
                <div className='col-md-2'>
                    <div className='d-none d-lg-block'>
                        {props.book.img ?
                            <img src={props.book.img} width='123' height='196' alt='Book'/>
                            :
                            <img src={require('./../../../Images/BooksImages/book-luv2code-1000.png')}
                                 width='123' height='196' alt='Book'/>
                        }
                    </div>
                    <div className='d-lg-none d-flex justify-content-center align-items-center'>
                        {props.book.img ?
                            <img src={props.book.img} width='123' height='196' alt='Book'/>
                            :
                            <img src={require('./../../../Images/BooksImages/book-luv2code-1000.png')}
                                 width='123' height='196' alt='Book'/>
                        }
                    </div>
                </div>
                <div className='col-md-6'>
                    <div className='card-body'>
                        <h5 className='card-title'>{props.book.author}</h5>
                        <h4>{props.book.title}</h4>
                        <p className='card-text'> {props.book.description} </p>
                    </div>
                </div>
                <div className='mt-3 col-md-4'>
                    <div className='d-flex justify-content-center align-items-center'>
                        <p>Total Quantity: <b>{quantity}</b></p>
                    </div>
                    <div className='d-flex justify-content-center align-items-center'>
                        <p>Books Remaining: <b>{remaining}</b></p>
                    </div>
                </div>
                <div className='mt-3 col-md-1'>
                    <div className='d-flex justify-content-start'>
                        <button onClick={deleteBook} className='m-1 btn btn-md btn-danger'>Delete</button>
                    </div>
                </div>
                <button onClick={() => changeQuantity(1)} className='m1 btn btn-md main-color text-white'>Add Quantity</button>
                <button onClick={() => changeQuantity(-1)} disabled={quantity <= 0} className='m1 btn btn-md btn-warning'>Decrease Quantity</button>
            </div>
        </div>
    );
};
