import { useEffect, useState } from "react"
import Image from "next/image"
export default function Contribute( {props} ) {
    const[values,setValues]=useState([]);
    useEffect(()=>{
        fetch("https://api.github.com/search/repositories?q=user:SaptarshiSarkar12+repo:Drifty+Drifty").then((res) => res.json())
        .then((data) => {
          setValues([data.items[0].stargazers_count,data.items[0].forks_count])
        })
    
    },[])
    return (
        <div id="contrib" className="md:flex md:flex-row justify-evenly bg-var text-white pb-5">
            <div className="md:w-1/2 md:pr-8">
                <h2 className="text-5xl sm:text-4xl font-bold lg:mt-20 md:mt-10 sm:pt-10 sm:mb-10 md:pl-20 xs:p-5 xs:text-center" >More Information About Drifty</h2>
                <p className="text-2xl lg:ml-20 md:ml-10 font-sans mb-8 leading-normal xs:px-4">
                    It is currently available in CLI (Command Line Interface) mode and
                    the GUI (Graphical User Interface) version is under active
                    development. We believe in team work. Any contribution that brings
                    value to the project is highly appreciated. You may contribute to
                    this project here.
                </p>
            </div>
            <div className="md:w-1/2">
            <h2 className="text-5xl sm:text-4xl font-bold md:mt-20 md:mb-20 md:pl-20 xs:text-center xs:mb-10" >Be a part of Drifty Family!</h2>
                <div className="grid lg:grid-cols-7 md:grid-cols-5 sm:grid-cols-8 xs:grid-cols-4 space-x-0 gap-y-4 justify-items-center xs:pb-10">
                {props.contrib.map((item,index) => {
                    return <a  href={item.html_url} key={index}><Image width={64} height={64} src={item.avatar_url} alt={item.login}></Image></a>
                })}
                </div>
                <div className="flex flex-cols-3 gap-7 text-lg font-semibold">
                <a className="md:rounded-full ml-2 xs:p-2  lg:p-2 border-2 md:border-white text-center   hover:text-black xs:transition ease-in-out delay-150 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300" href="https://github.com/SaptarshiSarkar12/Drifty"><i aria-hidden className="fab fa-github pr-1"></i>Contribute</a>
                <a className="md:rounded-full xs:p-2 lg:p-2 border-2 md:border-white text-center   hover:text-black xs:transition ease-in-out delay-150 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300" href="https://github.com/SaptarshiSarkar12/Drifty/stargazers"><i aria-hidden className="fa fa-regular fa-star pr-1"></i>Stars:{values[0]}</a>
                <a className="md:rounded-full xs:p-2 lg:p-2 border-2 md:border-white text-center   hover:text-black xs:transition ease-in-out delay-150 bg-blue-500 hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300" href="https://github.com/SaptarshiSarkar12/Drifty/network/members"><i aria-hidden className="fa fa-duotone fa-code-fork pr-1"></i>Forks:{values[1]}</a>
            </div>
            </div>
            
        </div>
    )
}
