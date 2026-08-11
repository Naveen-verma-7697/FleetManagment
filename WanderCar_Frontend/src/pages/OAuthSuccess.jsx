import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import * as customerService from "../api/customerService";
import { useAuth } from "../context/AuthContext";

export default function OAuthSuccess() {

    const navigate = useNavigate();

    const { completeOAuthLogin } = useAuth();

    useEffect(() => {

        async function finishLogin() {

            const params = new URLSearchParams(window.location.search);

            const token = params.get("token");

            if (!token) {
                navigate("/");
                return;
            }

            try {

                localStorage.setItem("token", token);

                const customer =
                    await customerService.getCurrentCustomer();

                completeOAuthLogin(customer, token);

            } catch (e) {

                console.error(e);

            }

            navigate("/");

        }

        finishLogin();

    }, [navigate, completeOAuthLogin]);

    return <h2>Signing you in...</h2>;
}