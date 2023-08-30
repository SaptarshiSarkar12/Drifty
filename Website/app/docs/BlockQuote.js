import React from "react";

const BlockQuote = ({ children }) => {
    return (
        <blockquote className="text-primary-medium">
            {children}
        </blockquote>
    );
};

export default BlockQuote;