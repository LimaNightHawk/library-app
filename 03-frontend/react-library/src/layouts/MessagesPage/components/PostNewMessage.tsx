import {useOktaAuth} from "@okta/okta-react";
import {useState} from "react";

export const PostNewMessage: React.FC<{}> = () => {

    const {authState} = useOktaAuth();
    const [title, setTitle] = useState('');
    const [question, setQuestion] = useState('');
    const [displayWarning, setDisplayWarning] = useState(false);
    const [displaySuccess, setDisplaySuccess] = useState(false);

    async function submitNewQuestion() {
        if (authState?.isAuthenticated && title !== '' && question !== '') {
            const url = `${process.env.REACT_APP_API}/messages/secure/message`;

            const requestOptions = {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                    'Content-type': 'application/json'
                },
                body: JSON.stringify({
                    title: title,
                    question: question
                })
            };
            const response = await fetch(url, requestOptions);
            if (!response.ok) {
                throw new Error('Failed message post');
            }
            setTitle('');
            setQuestion('');
            setDisplayWarning(false)
            setDisplaySuccess(true);
        } else {
            setDisplayWarning(true)
            setDisplaySuccess(false);
        }
    }

    return (
        <div className='card mt-3'>
            <div className='card-header'>
                Ask question to Luv 2 Read Admin
            </div>
            <div className='card-body'>
                <form method='POST'>
                    {displaySuccess &&
                        <div className='alert alert-success' role='alert'>
                            Question added successfully
                        </div>
                    }
                    {displayWarning &&
                        <div className='alert alert-danger' role='alert'>
                            All fields must be filled out
                        </div>
                    }

                    <div className='mb-3'>
                        <label className='form-label'>
                            Title
                        </label>
                        <input type='text' className='form-control' id='exampleFormControlInput1'
                               placeholder='Title' onChange={e => setTitle(e.target.value)} value={title}/>
                    </div>

                    <div className='mb-3'>
                        <label className='form-label'>
                            Question
                        </label>
                        <textarea className='form-control' id='exampleFormControlTextarea1'
                                  rows={3} onChange={e => setQuestion(e.target.value)} value={question}>
                        </textarea>
                    </div>
                    <div>
                        <button onClick={submitNewQuestion} type='button' className='btn btn-primary mt-3'>
                            Submit Question
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}