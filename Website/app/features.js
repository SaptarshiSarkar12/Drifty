import Image from "next/image";

export default function Features() {
    return (
        <div id="features" className="bg-bottom">
            <h1 className="select-none text-center font-extrabold text-5xl pt-6">Features</h1>
            <h1 className={"text-center text-black font-semibold text-xl"}>Take a look at the features of Drifty</h1>
            <p className={"grid grid-cols-2 py-3"}>
                <p className={"text-center text-black font-semibold text-2xl"}>It&apos;s Free & Open-Source</p>
                <Image className={"content-center"} src={"images/OSS.webp"} alt={"Drifty is completely Open-Source"} width={64} height={64}></Image>
            </p>
        </div>
    )
}