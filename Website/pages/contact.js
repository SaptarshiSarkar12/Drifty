import Footer from "/components/Footer";
import Header from "/components/Header";

export default function contact(){
    return(
        <div>
           <Header props={"bg-about"}/>
            <div className="bg-about py-5">
              <h1 className=" text-center font-semibold text-7xl">Contact Us</h1>
              <p className="text-center font-normal pt-5 pb-8 text-lg">We`d love to hear from you!</p>
              <form className="grid grid-cols-2 justify-items-center" method="POST" action="https://formsubmit.co/e94b201f9a607081e9f9f8ee09ff5e25">
          <input className="md: w-9/12 sm:w-1/2 h-14 p-3 col-span-2 bg-input text-white" type="text" name="name" required placeholder="Name"/>
          <input className="mt-10 md:ml-auto md:mr-2  md: w-1/2 justify-end h-14 md:col-span-1 col-span-2 p-2 bg-input text-white" type="email" name="email" required placeholder="Email"/>
          <input className="mt-10 md:mr-auto md:ml-2 md:w-1/2 justify-start  h-14 p-2  md:col-span-1 col-span-2 bg-input text-white" type="number" name="phone" required placeholder="Phone"/>
          
         <textarea className="md:w-9/12 sm:w-1/2 h-64  col-span-2 mt-10 p-6 bg-input text-white" name="message" placeholder="Message"></textarea>
        <button className="col-span-2 mt-10 border-2 border-white" type="submit" >Send message</button>
      </form>
            </div>
            <Footer/>
        </div>
    )
}