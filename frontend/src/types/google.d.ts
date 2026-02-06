declare namespace google {
  namespace accounts {
    namespace id {
      interface IdConfiguration {
        client_id: string;
        callback: (response: CredentialResponse) => void;
        auto_select?: boolean;
        cancel_on_tap_outside?: boolean;
        context?: "signin" | "signup" | "use";
      }

      interface CredentialResponse {
        credential: string;
        select_by: string;
      }

      function initialize(config: IdConfiguration): void;
      function prompt(): void;
      function renderButton(
        parent: HTMLElement,
        options: {
          type?: "standard" | "icon";
          theme?: "outline" | "filled_blue" | "filled_black";
          size?: "large" | "medium" | "small";
          text?: "signin_with" | "signup_with" | "continue_with";
          shape?: "rectangular" | "pill" | "circle" | "square";
          logo_alignment?: "left" | "center";
          width?: number;
          locale?: string;
        },
      ): void;
    }
  }
}
