"use client";

import {useEffect, useState} from "react"
import Image from "next/image"
import Link from "next/link";

export default function Contribute({ props }) {
    const [values, setValues] = useState([]);
    useEffect(() => {
        fetch("https://api.github.com/search/repositories?q=user:SaptarshiSarkar12+repo:Drifty+Drifty").then((res) => res.json())
            .then((data) => {
                setValues([data.items[0].stargazers_count, data.items[0].forks_count])
            })
    }, [])
    let totalNoOfContributors = props.contrib.length;
    return (
        <div id="contribute" className="md:grid md:grid-cols-2 justify-center bg-var select-none text-white pb-5">
            <div className="md:pr-8 mb-2">
                <h2 className="text-3xl mb-3 sm:text-4xl font-bold lg:mt-16 md:mt-10 sm:pt-10 sm:mb-10 xs:p-5 text-center" >More Information About Drifty</h2>
                <p className="text-2xl text-center ml-5 mr-5 font-sans mb-8 leading-normal xs:px-4">
                    It is available both in CLI (Command Line Interface) and
                    GUI (Graphical User Interface) mode for all the major platforms like
                    Windows, Linux and MacOS. We believe in team work. Any contribution that brings
                    value to the project is highly appreciated.
                    You can look into the <Link className={"font-bold bg-gradient-to-r from-pink-500 to-yellow-400 text-transparent bg-clip-text"} target={"_blank"} href={"https://github.com/users/SaptarshiSarkar12/projects/3"}>roadmap</Link> to know about the issues to work on,
                    <b className={"bg-gradient-to-r from-green-500 to-green-400 text-transparent bg-clip-text"}> in progress</b> and completed.
                </p>
            </div>
            <div className="ml-2">
                <h2 className="text-3xl mb-3 sm:text-4xl font-bold lg:mt-16 md:mt-10 sm:pt-10 sm:mb-10 xs:p-5 text-center" >Be a part of Drifty Family!</h2>
                <div className="flex items-center justify-center gap-[2em] flex-wrap rounded-full space-x-0 gap-y-4 justify-items-center xs:pb-5 pr-1">
                    {props.contrib.map((item, index) => {
                        if (index < 7 && item.type === "User") { // We are using 7 instead of 6 because we are not counting the dependabot
                            return <a href={item.html_url} key={index}><Image
                                className="rounded-full hover:transition ease-in-out hover:-translate-y-1 hover:scale-110 delay-150 duration-300"
                                width={64} height={64} src={item.avatar_url + ".webp&s=77"}
                                alt={item.login}></Image></a>
                        }
                    })}
                    <a href="https://github.com/SaptarshiSarkar12/Drifty/graphs/contributors" target="_blank" >
                        <div className="gap-1 rounded-full h-16 w-16 text-xl font-semibold flex items-center justify-center border-2 md:border-white text-center   hover:text-black xs:transition ease-in-out delay-150 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300">
                            {totalNoOfContributors - 6}+
                        </div>
                    </a>
                </div>
                <div className="flex items-center justify-center flex-wrap text-lg font-semibold pr-3 gap-6 mt-4">
                    <a className="rounded-lg xs:p-[3px] sm:p-1 w-auto lg:w-48 border-2 md:border-white text-center hover:text-black xs:transition ease-in-out delay-100 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300" href="https://github.com/SaptarshiSarkar12/Drifty"><i aria-hidden="true" className="fab fa-github p-2"></i>Contribute</a>
                    <a className="rounded-lg xs:p-[3px] sm:p-1 w-auto lg:w-48 border-2 md:border-white text-center hover:text-black xs:transition ease-in-out delay-100 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300" href="https://github.com/SaptarshiSarkar12/Drifty/stargazers"><i aria-hidden="true" className="fa fa-regular fa-star p-2"></i>Stars : {values[0]}</a>
                    <a className="rounded-lg xs:p-[3px] sm:p-1 w-auto lg:w-48 border-2 md:border-white text-center hover:text-black xs:transition ease-in-out delay-100 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300" href="https://github.com/SaptarshiSarkar12/Drifty/network/members"><i aria-hidden="true" className="fa fa-duotone fa-code-fork p-2"></i>Forks : {values[1]}</a>
                </div>
            </div>
        </div>
    )
}

