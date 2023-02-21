export default function Contribute({props}) {
    // console.log(props)
    return (
        <div>
            <span>
                <h2 >More Information About Drifty</h2>
                <p>
                    It is currently available in CLI (Command Line Interface) mode and
                    the GUI (Graphical User Interface) version is under active
                    development. We believe in team work. Any contribution that brings
                    value to the project is highly appreciated. You may contribute to
                    this project here.
                </p>
            </span>
            
                {props.map((item,index)=>{
                    return <h1>{item.login}</h1>
                })}
            
        </div>
    )
}
