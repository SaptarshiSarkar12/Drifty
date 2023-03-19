import { useEffect, useState } from "react"
import { remark } from 'remark';
import html from 'remark-html';

function alertfunction(b){
    if(confirm("Do You want to Download Drifty?")){
        alert("Thanks for Downloading");
        window.open(b? "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty_CLI.exe"
        :"https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty.jar")
    }
}

export default function Download({ props }) {
    const [buttonStates, setButtonStates] = useState({});
    const [content,setContent]=useState([]);

    const handleButtonClick = (index) => {
        setButtonStates((prevState) => ({
            ...prevState,
            [index]: !prevState[index],
        }));
    };
    const markertoHtml = async (itemBody,i) => {
        const cont = await remark().use(html).process(itemBody);
        setContent((prev)=>{prev[i]=cont.toString(); return prev});
        
    }
    return (
        <div id="download" className="bg-bottom">
            <h2 className="text-5xl sm:text-4xl font-bold md:mt-2 sm:pt-10 sm:mb-10 md:pl-20 xs:p-5 xs:text-center" >Download Here</h2>
            <div className="md:flex md:space-x-10 justify-center pl-10">
                <a className="md:inline-block xs:block md:pl-8 md:py-5 md:w-80 md:h-20 bg-blue-700 text-white font-semibold md:text-3xl leading-tight rounded-full  hover:bg-yellow-400 hover:drop-shadow-lg focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition duration-0 ease-in-out" onClick={()=>alertfunction(true)} role="button" >Download Now <i className="fab fa-brands fa-windows"></i></a>
                <a className="md:inline-block xs:block md:pl-8 md:py-5 md:w-80 md:h-20 bg-blue-700 text-white font-semibold md:text-3xl leading-tight rounded-full  hover:bg-yellow-400 hover:drop-shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition duration-0 ease-in-out" onClick={()=>alertfunction(false)} role="button" >Download Now <i class="fab fa-brands fa-apple"></i> <i class="fab fa-brands fa-linux"></i></a>
            </div>
            <p className=" text-center font-bold pt-10 pl-9">All Releases</p>
            {props.release.map((item, index) => {
                useEffect(()=>{
                    markertoHtml(item.body,index);
                },[]);
                return <div key={index} className="text-center p-5 text-base font-normal">
                    <span className="font-bold">{item.name} </span>
                    {index == 0 && <span className="p-1 rounded-3xl bg-green-500">Latest</span>}
                    <p>{new Date(item.published_at).toString()} with {item.assets[0].download_count+item.assets[1].download_count} Downloads</p>
                    <button onClick={() => handleButtonClick(index)} className="opacity-50">{buttonStates[index] ? "Hide" : "Learn More"}</button>
                    {buttonStates[index] && <div className="p-5"  dangerouslySetInnerHTML={{ __html:content[index]}}></div>}
                    <div className="grid grid-flow-col gap-52 justify-center text-white mt-3 font-semibold">
                        <a className="md:w-28 md:h-8 bg-blue-900 rounded-full p-1 shadow-none hover:drop-shadow-2xl" href={item.assets[1].browser_download_url}>Download <i className="fab fa-brands fa-windows"></i></a>
                        <a className="md:w-32 md:h-8 bg-blue-900 rounded-full p-1 shadow-none hover:drop-shadow-2xl" href={item.assets[0].browser_download_url}>Download <i class="fab fa-brands fa-apple"></i> <i class="fab fa-brands fa-linux"></i></a>
                    </div>
                </div>
            })}
        </div>
    )
}