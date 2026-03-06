import { GoogleLogin } from "@react-oauth/google";
import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import { Link, useNavigate } from "react-router";
import { login, register } from "../api/AuthApi";
import { useAuth } from "../hooks/useAuth";
import { EyeOff, Eye } from "lucide-react";

const RegisterPage = () => {
    const { loginWithGoogle } = useAuth();
    const navigate = useNavigate();

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [timezone, setTimezone] = useState(
        Intl.DateTimeFormat().resolvedOptions().timeZone,
    );
    const [isLoading, setIsLoading] = useState(false);

    const timeZones = Intl.supportedValuesOf("timeZone");

    const getErrorMessage = (error: unknown) =>
        axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Something went wrong.")
            : "Something went wrong.";

    const handleRegister = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await register({
                firstName,
                lastName,
                email,
                password,
                timeZone: timezone,
            });
            await login(email, password);
            navigate("/events");
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-base-200 flex items-center justify-center px-4">
            <div className="w-full max-w-sm flex flex-col gap-6">
                {/* Card */}
                <div className="bg-base-100 rounded-2xl shadow-sm border border-base-300 p-8 flex flex-col gap-5">
                    {/* Form */}
                    <form
                        onSubmit={handleRegister}
                        className="flex flex-col gap-4"
                    >
                        {/* Name Row */}
                        <div className="flex gap-3">
                            <div className="flex flex-col gap-1.5 flex-1">
                                <label className="text-sm font-medium text-base-content">
                                    First name
                                </label>
                                <input
                                    type="text"
                                    className="input input-bordered w-full"
                                    placeholder="John"
                                    value={firstName}
                                    onChange={(e) =>
                                        setFirstName(e.target.value)
                                    }
                                    required
                                />
                            </div>
                            <div className="flex flex-col gap-1.5 flex-1">
                                <label className="text-sm font-medium text-base-content">
                                    Last name
                                </label>
                                <input
                                    type="text"
                                    className="input input-bordered w-full"
                                    placeholder="Doe"
                                    value={lastName}
                                    onChange={(e) =>
                                        setLastName(e.target.value)
                                    }
                                    required
                                />
                            </div>
                        </div>

                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium text-base-content">
                                Email
                            </label>
                            <input
                                type="email"
                                className="input input-bordered w-full"
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium text-base-content">
                                Password
                            </label>
                            <div className="relative">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    className="input input-bordered w-full pr-10"
                                    placeholder="••••••••"
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(e.target.value)
                                    }
                                    required
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-base-content/40 hover:text-base-content transition-colors"
                                    onClick={() => setShowPassword((v) => !v)}
                                >
                                    {showPassword ? (
                                        <EyeOff className="w-4 h-4" />
                                    ) : (
                                        <Eye className="w-4 h-4" />
                                    )}
                                </button>
                            </div>
                        </div>

                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium text-base-content">
                                Timezone
                            </label>
                            <select
                                className="select select-bordered w-full"
                                value={timezone}
                                onChange={(e) => setTimezone(e.target.value)}
                                required
                            >
                                {timeZones.map((tz) => (
                                    <option key={tz} value={tz}>
                                        {tz}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full mt-1"
                            disabled={isLoading}
                        >
                            {isLoading ? (
                                <span className="loading loading-spinner loading-sm" />
                            ) : (
                                "Create account"
                            )}
                        </button>
                    </form>

                    {/* Divider */}
                    <div className="divider text-xs text-base-content/40">
                        or
                    </div>

                    {/* Google */}
                    <GoogleLogin
                        onSuccess={(res) => {
                            loginWithGoogle(res.credential!)
                                .then(() => navigate("/events"))
                                .catch((error) =>
                                    toast.error(getErrorMessage(error)),
                                );
                        }}
                        onError={() => toast.error("Google sign-in failed.")}
                    />
                </div>

                {/* Login link */}
                <p className="text-center text-sm text-base-content/50">
                    Already have an account?{" "}
                    <Link
                        to="/login"
                        className="text-primary font-medium hover:underline"
                    >
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;
