import { Inter } from '@next/font/google'
import Header from '../components/Header'
import Contribute from '../components/Contribute'
import Demo from '../components/Demo'
import Footer from '../components/Footer'
import MainSection from '/components/MainSection'


const inter = Inter({ subsets: ['latin'] })

export default function Home(props) {
  return (
    <>
      <Head />
      <Header props={"bg-top"} />
      <MainSection />
      <Contribute props={props.contribs}/>
      <Demo />
      <Footer />
    </>
  )
}
export async function getStaticProps(){
  const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors',{method:'GET'},{next:{revalidate:3600}})
    const contrib= await res.json();
    return {
        props:{contribs:{contrib}},
        revalidate:3600
    }
}
