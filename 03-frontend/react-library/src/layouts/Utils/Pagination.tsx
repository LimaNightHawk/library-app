import React from 'react';

interface Props {
    currentPage: number;
    totalPages: number;
    paginate: any;

}


export const Pagination: React.FC<Props> = ({currentPage, paginate, totalPages}) => {


    const pagesEachSide = 2;
    const pageNumbers: number[] = [];

    const startPage = Math.max(1, currentPage - pagesEachSide);
    const endPage = Math.min(totalPages, currentPage + pagesEachSide);
    for (let page = startPage; page <= endPage; page++) {
        pageNumbers.push(page);
    }


    return (
        <nav aria-label='...'>
            <ul className='pagination'>
                <li className='page-item' onClick={() => paginate(1)}>
                    <button className='page-link'>
                        First page
                    </button>
                </li>
                {pageNumbers.map(page => (
                    <li key={page} onClick={() => paginate(page)} className={'page-item' + (currentPage === page ? 'active' : '')}>
                        <button className='page-link'>

                            {page}
                        </button>
                    </li>
                ))}
                <li className='page-item' onClick={() => paginate(totalPages)}>
                    <button className='page-link'>
                        Last page
                    </button>
                </li>
            </ul>
        </nav>
    );
};
