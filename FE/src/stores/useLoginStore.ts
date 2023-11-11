import { LoginStore, ResLogin } from "@/types/CommonType";
import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

const StorageKey = "user-info";
// export let user: ResLogin | undefined;

export const useLoginStore = create(
  persist<LoginStore>(
    (set) => ({
      user: null,
      userLogin: (res: ResLogin) => set({ user: res }),
      userLogout: () => set({ user: null }),
    }),
    {
      name: StorageKey,
      storage: createJSONStorage(() => sessionStorage),
    }
  )
);