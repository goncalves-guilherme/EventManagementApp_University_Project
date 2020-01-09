using SilentManager.API.Security;
using SilentManagerApiServer.DataAccess;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Validations
{
    public class TokenValidation
    {
        public static bool ValidateToken(TokenDto token, IUnitOfWork _unitOfWork)
        {
            return _unitOfWork.tokenRepo.Find(tk => tk.token.Equals(token.AccessToken) && tk.Expiration_Date >= DateTime.Now).Any();   
        }

        public static int GetUserIdFromToken(TokenDto token, IUnitOfWork _unitOfWork) {
            // Get user from token
            return _unitOfWork.tokenRepo.Find(tk => tk.token.Equals(token.AccessToken)).FirstOrDefault().UserId.Value;
        }
    }
}
