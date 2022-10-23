import { getWithJWT } from '../../utils/requestUtils';

export const load = async ({ fetch }) => {
    
    
    const fetchSetup = async () => {
        const [setup, error] = await getWithJWT("http://localhost:8082/api/setup", 200);
        return [setup, error]
    }
        
    return {
        setup: fetchSetup()
    }
}
