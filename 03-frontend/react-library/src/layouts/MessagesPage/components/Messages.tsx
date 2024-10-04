import React, {useEffect, useState} from 'react';
import {useOktaAuth} from "@okta/okta-react";
import {MessageModel} from "../../../models/MessagesModel";
import {API_URL} from "../../../constants";
import {SpinnerLoading} from "../../Utils/SpinnerLoading";
import {Pagination} from "../../Utils/Pagination";

export const Messages: React.FC<{}> = () => {

    const {authState} = useOktaAuth();
    const [isLoadingmessages, setIsLoadingmessages] = useState(true);
    const [httpError, setHttpError] = useState(null);

    // Messages
    const [messages, setMessages] = useState<MessageModel[]>([]);

    //Pagination
    const [messagesPerPage] = useState(5);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchUserMessages = async () => {
            if (authState && authState.isAuthenticated) {
                const url = API_URL + `/messages/search/findByUserEmail?userEmail=${authState.accessToken?.claims.sub}&page=${currentPage - 1}&size=${messagesPerPage}`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                        'Content-Type': 'application/json'
                    }
                }
                const response = await fetch(url, requestOptions);
                if (!response.ok) {
                    throw new Error("Error trying to fetch UserMessages");
                }
                const responseJson = await response.json();
                setMessages(responseJson._embedded.messages);
                setTotalPages(responseJson.page.totalPages);
            }
            setIsLoadingmessages(false);
        }
        fetchUserMessages().catch((error: any) => {
            setIsLoadingmessages(false);
            setHttpError(error.message);
        });
        window.scrollTo(0, 0);

    }, [authState, currentPage]);

    if (isLoadingmessages) {
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

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return (
        <div className='mt-2'>
            {messages.length > 0 ?
                <>
                    <h5>Current Q/A: </h5>
                    {messages.map(message => (
                        <div key={message.id}>
                            <div className='card mt-2 shadow p-3 bg-body rounded'>
                                <h5>Case #{message.id}: {message.title}</h5>
                                <h6>{message.userEmail}</h6>
                                <p>{message.question}</p>
                                <hr/>
                                <div>
                                    <h5>Response: </h5>
                                    {message.response && message.adminEmail ?
                                        <>
                                            <h6>{message.adminEmail} (admin)</h6>
                                            <p>{message.response}</p>
                                        </>
                                        :
                                        <p><i>Pending response from administration. Please be patient.</i></p>
                                    }
                                </div>
                            </div>
                        </div>
                    ))}
                </>
                :
                <h5>All questions you submit will be shown here</h5>
            }
            {totalPages > 1 && <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate}/>}
        </div>
    );
};
