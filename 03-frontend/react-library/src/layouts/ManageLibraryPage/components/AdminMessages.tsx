import React, {useEffect, useState} from 'react';
import {useOktaAuth} from "@okta/okta-react";
import {MessageModel} from "../../../models/MessagesModel";
import {SpinnerLoading} from "../../Utils/SpinnerLoading";
import {Pagination} from "../../Utils/Pagination";
import {AdminMessage} from "./AdminMessage";
import {AdminMessageRequest} from "../../../models/AdminMessageRequest";

export const AdminMessages: React.FC<{}> = () => {

    const {authState} = useOktaAuth();

    // Normal Loading Pieces
    const [isLoadingMessages, setIsLoadingMessages] = useState(true);
    const [httpError, setHttpError] = useState(null);

    // Messages endpoint state
    const [messages, setMessages] = useState<MessageModel[]>([]);
    const [messagesPerPage] = useState(5);

    // Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    //recall useEffect
    const [btnSubmit, setBtnSubmit] = useState(false);


    useEffect(() => {
        const fetchMessages = async () => {
            if (authState && authState.isAuthenticated) {
                const url = `${process.env.REACT_APP_API}/messages/search/findByClosed?closed=false&page=${currentPage - 1}&size=${messagesPerPage}`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                        'Content-Type': 'application/json'
                    }
                }
                const response = await fetch(url, requestOptions);
                if (!response.ok) {
                    throw new Error("Error trying to fetch Messages");
                }
                const responseJson = await response.json();
                setMessages(responseJson._embedded.messages);
                setTotalPages(responseJson.page.totalPages);
            }
            setIsLoadingMessages(false);
        }
        fetchMessages().catch((error: any) => {
            setIsLoadingMessages(false);
            setHttpError(error.message);
        });
        window.scrollTo(0, 0);
    }, [authState, currentPage, btnSubmit]);

    if (isLoadingMessages) {
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

    async function submitResponseToQuestion(id: number, answer: string) {

        if (authState?.isAuthenticated && id !== null && answer !== '') {
            const url = `${process.env.REACT_APP_API}/messages/secure/admin/message`;
            const messageAdminRequestModel: AdminMessageRequest = new AdminMessageRequest(id, answer);
            const requestOptions = {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(messageAdminRequestModel)
            }
            const response = await fetch(url, requestOptions);
            if (!response.ok){
                throw new Error("Error trying to post response.")
            }
            setBtnSubmit(!btnSubmit);
        }
    }

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return (
        <div className='mt-3'>
            {messages.length > 0 ?
                <>
                    <h5>Pending Q/A: </h5>
                    {messages.map(message => (
                        <AdminMessage message={message} key={message.id} submitResponseToQuestion={submitResponseToQuestion}/>
                    ))}
                </>
                :
                <h5>No pending Q/A</h5>
            }
            {totalPages > 1 && <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate}/>}
        </div>
    );
};

