import { MDXProvider } from "@mdx-js/react";
import

export default function Docs() {
    return (
        <div className={"bg-gradient-to-b from-top from-8% via-cyan-300 to-bottom to-12% -mt-2"}>
            <h1 className={"select-none text-5xl text-center sm:text-4xl font-bold md:mt-2 sm:pt-10 sm:mb-10 xs:p-5"}>Documentation</h1>
            <MDXProvider components={}>

            </MDXProvider>
        </div>
    )
}