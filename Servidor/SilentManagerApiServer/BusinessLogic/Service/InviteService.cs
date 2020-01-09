using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using SilentManager.API.BusinessLogic.Dtos;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.DataAccess;
using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using SilentManagerApiServer.Models.DB;
using SilentManager.API.Security;
using SilentManager.API.BusinessLogic.Validations;

namespace SilentManager.API.BusinessLogic.Service
{
    public class InviteService : IInviteService
    {

        private readonly IUnitOfWork _unitOfWork;

        public InviteService(IUnitOfWork unitOfWork)
        {
            this._unitOfWork = unitOfWork;
        }

        public async Task<ResultWrapper<InviteOutDto>> CreateInvite(InviteDto newInvite, TokenDto token)
        {
            if (!TokenValidation.ValidateToken(token, _unitOfWork))
            {
                List<ErrorResult> errors = new List<ErrorResult>();
                errors.Add(new ErrorResult(401, "Invalid token."));
                _unitOfWork.tokenRepo.Remove(_unitOfWork.tokenRepo.Find(tk => tk.token.Equals(token.AccessToken)).FirstOrDefault());
                return new ResultWrapper<InviteOutDto>(null, errors);
            }

            var invite = GetInvite(newInvite);

            if (invite != null)
            {
                invite.State = newInvite.State;
            }
            else
            {
                invite = Mapper.Map<Invite>(newInvite);
                _unitOfWork.InviteRepo.AddAsync(invite);
            }

            await _unitOfWork.InviteRepo.SaveAsync();

            var eventI = await _unitOfWork.EventRepo.GetAsync(invite.EventId);
            var user = await _unitOfWork.UserRepo.GetAsync(invite.UserId);

            return new ResultWrapper<InviteOutDto>(ConvertInviteToOutInvite(user, eventI, invite.State), null);
        }

        private InviteOutDto ConvertInviteToOutInvite(SilentManagerApiServer.Models.DB.User user, Event eventI, int? state)
        {
            return new InviteOutDto
            {
                User = new UserOutDto
                {
                    UserId = user.UserId,
                    Email = user.Email,
                    Name = user.Name
                },
                Event = new Models.DB.CompletedEvent
                {
                    Name = eventI.Name,
                    Description = eventI.Description,
                    EventId = eventI.EventId
                },
                State = state
            };
        }

        public async Task<ResultWrapper<IEnumerable<InviteOutDto>>> GetInvites(TokenDto token, int? eventId, int? state)
        {
            if (token == null || !TokenValidation.ValidateToken(token, _unitOfWork))
            {
                List<ErrorResult> errors = new List<ErrorResult>();
                errors.Add(new ErrorResult(401, "Invalid token."));
                _unitOfWork.tokenRepo.Remove(_unitOfWork.tokenRepo.Find(tk => tk.token.Equals(token.AccessToken)).FirstOrDefault());
                return new ResultWrapper<IEnumerable<InviteOutDto>>(null, errors);
            }

            int? userId = null;
            if (eventId == null)
            {
                userId = TokenValidation.GetUserIdFromToken(token, _unitOfWork);
            }

            var invites = _unitOfWork.InviteRepo.GetInvites(userId, eventId, state);

            return new ResultWrapper<IEnumerable<InviteOutDto>>(Mapper.Map<IEnumerable<InviteOutDto>>(invites), null);
        }

        public async Task<IEnumerable<InviteStateDto>> GetInviteState()
        {
            var inviteState = _unitOfWork.InviteRepo.GetAllInviteState();
            return Mapper.Map<IEnumerable<InviteStateDto>>(inviteState);
        }

        private Invite GetInvite(InviteDto newInvite)
        {
            return _unitOfWork.InviteRepo.Find(iv => iv.UserId == newInvite.UserId && iv.EventId == newInvite.EventId).FirstOrDefault();
        }

        public async Task<bool> DeleteInvite(InviteDto inviteToDelete, TokenDto token)
        {
            if (!TokenValidation.ValidateToken(token, _unitOfWork))
            {
                List<ErrorResult> error = new List<ErrorResult>();
                _unitOfWork.tokenRepo.Remove(_unitOfWork.tokenRepo.Find(tk => tk.token.Equals(token.AccessToken)).FirstOrDefault());
                error.Add(new ErrorResult(401, "Invalid token."));
                return false;
            }


            Invite deleteInvite = _unitOfWork.InviteRepo.Find(i => i.UserId == inviteToDelete.UserId && i.EventId == inviteToDelete.EventId).FirstOrDefault();
            _unitOfWork.InviteRepo.Remove(deleteInvite);

            return await _unitOfWork.InviteRepo.SaveAsync() > 0;
        }
    }
}
